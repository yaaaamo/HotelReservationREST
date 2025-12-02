package com.example.agence_service.cli;

import com.example.agence_service.model.Hotel;
import com.example.agence_service.model.AvailabilityRequest;
import com.example.agence_service.model.AvailabilityOffer;
import com.example.agence_service.model.BookingRequest;
import com.example.agence_service.model.BookingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class HotelRestClientCLI extends AbstractMain implements CommandLineRunner {

  @Autowired
  private RestTemplate proxy;

  @Value("${hotel1.base-url}")
  private String hotel1BaseUrl;

  @Value("${hotel2.base-url}")
  private String hotel2BaseUrl;

  private IntegerInputProcessor inputProcessor;

  private static String URI_HOTELS;

  // list of partner hotel base URLs
  private List<String> partnerBaseUrls;

  // to remember from which hotel each offer comes
  private static class PartnerOffer {
    String baseUrl;
    AvailabilityOffer offer;

    PartnerOffer(String baseUrl, AvailabilityOffer offer) {
      this.baseUrl = baseUrl;
      this.offer = offer;
    }
  }

  private List<PartnerOffer> lastAggregatedOffers = new ArrayList<>();

  @Override
  public void run(String... args) throws Exception {
    BufferedReader inputReader = null;
    String userInput = "";

    try {
      inputReader = new BufferedReader(new InputStreamReader(System.in));

      // init partner URLs (agency knows its partners, no question to the user)
      partnerBaseUrls = Arrays.asList(hotel1BaseUrl, hotel2BaseUrl);

      // we keep URI_HOTELS using the first hotel (for options 1 and 2)
      SERVICE_URL = hotel1BaseUrl;
      URI_HOTELS = SERVICE_URL + "/hotels";

      do {
        menu();
        userInput = inputReader.readLine();
        processUserInput(inputReader, userInput, proxy);
        Thread.sleep(3000);
      } while(!userInput.equals(QUIT));

    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected boolean validServiceUrl() {
    // no longer used, but must be implemented
    return true;
  }

  @Override
  protected void menu() {
    StringBuilder builder = new StringBuilder();
    builder.append(QUIT + ". Quit.");
    builder.append("\n1. Get number of hotels (from first partner).");
    builder.append("\n2. Display all hotels (from first partner).");
    builder.append("\n3. Check availability (all partner hotels).");
    builder.append("\n4. Book an offer from last search.");

    System.out.println(builder);
  }

  private void processUserInput(BufferedReader reader, String userInput, RestTemplate proxy) {
    inputProcessor = new IntegerInputProcessor(reader);

    try {
      switch(userInput) {

        case "1": {
          String uri = URI_HOTELS + "/count";
          String countStr = proxy.getForObject(uri, String.class);
          ObjectMapper mapper = new ObjectMapper();
          long count = (int) mapper.readValue(countStr, Map.class).get("count");
          System.out.println(String.format("There are %d hotels (on first partner)", count));
          System.out.println();
          break;
        }

        case "2": {
          String uri = URI_HOTELS;
          Hotel[] hotels = proxy.getForObject(uri, Hotel[].class);
          System.out.println("Hotels from first partner:");
          Arrays.asList(hotels).forEach(System.out::println);
          System.out.println();
          break;
        }

        // ========= WS1 : consulter disponibilités (TOUS LES HOTELS PARTENAIRES) =========
        case "3": {
          System.out.println("=== Check availability on all partner hotels ===");

          System.out.print("Start date (yyyy-MM-dd): ");
          LocalDate start = LocalDate.parse(reader.readLine());

          System.out.print("End date (yyyy-MM-dd): ");
          LocalDate end = LocalDate.parse(reader.readLine());

          inputProcessor.setMessage();
          System.out.println("Number of persons : ");
          int nbPers = inputProcessor.process();

          AvailabilityRequest req = new AvailabilityRequest();
          // agency credentials (you can externalize later)
          req.setAgenceId("AGENCE1");
          req.setPassword("secret");
          req.setDateDebut(start);
          req.setDateFin(end);
          req.setNbPersonnes(nbPers);

          lastAggregatedOffers.clear();

          // Call each partner hotel
          for (String baseUrl : partnerBaseUrls) {
            try {
              String uri = baseUrl + "/availability";
              AvailabilityOffer[] offers = proxy.postForObject(uri, req, AvailabilityOffer[].class);

              if (offers != null) {
                for (AvailabilityOffer o : offers) {
                  lastAggregatedOffers.add(new PartnerOffer(baseUrl, o));
                }
              }
            } catch (Exception e) {
              System.err.println("[WARN] Could not contact partner: " + baseUrl + " -> " + e.getMessage());
            }
          }

          if (lastAggregatedOffers.isEmpty()) {
            System.out.println("No offers found from any partner.");
          } else {
            System.out.println("Available offers from all partners:");
            int idx = 1;
            for (PartnerOffer po : lastAggregatedOffers) {
              AvailabilityOffer o = po.offer;
              System.out.println(idx + ") ["
                      + po.baseUrl + "] "
                      + "Offer=" + o.getOfferId()
                      + ", Hotel=" + o.getHotelName()
                      + ", beds=" + o.getNbLits()
                      + ", from " + o.getDateDebut()
                      + " to " + o.getDateFin()
                      + ", price=" + o.getPrix());
              idx++;
            }
          }
          System.out.println();
          break;
        }

        // ========= WS2 : effectuer réservation (SUR LE BON HOTEL) =========
        case "4": {
          System.out.println("=== Book an offer from last availability search ===");

          if (lastAggregatedOffers.isEmpty()) {
            System.out.println("No offers available. Please run option 3 first.");
            System.out.println();
            break;
          }

          // show offers again with their IDs, so user can pick one
          System.out.println("Available offers:");
          for (PartnerOffer po : lastAggregatedOffers) {
            AvailabilityOffer o = po.offer;
            System.out.println(" - OfferId=" + o.getOfferId()
                    + ", Hotel=" + o.getHotelName()
                    + ", beds=" + o.getNbLits()
                    + ", from " + o.getDateDebut()
                    + " to " + o.getDateFin()
                    + ", price=" + o.getPrix());
          }
          System.out.println();

          // 🔥 ask by offerId instead of number
          System.out.print("Enter the offer ID to book (e.g. H1-R101): ");
          String chosenId = reader.readLine().trim();

          PartnerOffer selected = lastAggregatedOffers.stream()
                  .filter(po -> po.offer.getOfferId().equals(chosenId))
                  .findFirst()
                  .orElse(null);

          if (selected == null) {
            System.out.println("No offer found with ID " + chosenId);
            System.out.println();
            break;
          }

          System.out.print("Client first name: ");
          String prenom = reader.readLine();

          System.out.print("Client last name: ");
          String nom = reader.readLine();

          System.out.print("Client email: ");
          String email = reader.readLine();

          System.out.print("Client phone: ");
          String tel = reader.readLine();

          BookingRequest req = new BookingRequest();
          req.setAgenceId("AGENCE1");
          req.setLogin("agencyLogin");
          req.setPassword("secret");
          req.setOfferId(selected.offer.getOfferId());  // e.g. "H1-R101"
          req.setPrenom(prenom);
          req.setNom(nom);
          req.setEmail(email);
          req.setTelephone(tel);

          // Call the /reservations endpoint on the SAME HOTEL that gave this offer
          String uri = selected.baseUrl + "/reservations";
          BookingResponse resp = proxy.postForObject(uri, req, BookingResponse.class);

          if (resp == null) {
            System.out.println("No response from server.");
          } else {
            System.out.println("Server response: " + resp.getMessage());
            if (resp.isSuccess()) {
              System.out.println("Reservation reference: " + resp.getReservationRef());
            }
          }
          System.out.println();
          break;
        }

        default:
          break;
      }

    } catch (HttpClientErrorException e) {
      System.err.println(e.getStatusCode() + ": " + e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
