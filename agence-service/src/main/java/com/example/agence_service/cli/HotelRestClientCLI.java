package com.example.agence_service.cli;

import com.example.agence_service.model.Hotel;
import com.example.agence_service.model.AvailabilityRequest;
import com.example.agence_service.model.AvailabilityOffer;
import com.example.agence_service.model.BookingRequest;
import com.example.agence_service.model.BookingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class HotelRestClientCLI extends AbstractMain implements CommandLineRunner {

  @Autowired
  private RestTemplate proxy;

  private IntegerInputProcessor inputProcessor;

  private static String URI_HOTELS;

  @Override
  public void run(String... args) throws Exception {
    BufferedReader inputReader;
    String userInput = "";
    try {
      inputReader = new BufferedReader(new InputStreamReader(System.in));
      setTestServiceUrl(inputReader);

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
    // ton URL REST existante
    return SERVICE_URL.equals("http://localhost:8080/hotelservice/api");
  }

  @Override
  protected void menu() {
    StringBuilder builder = new StringBuilder();
    builder.append(QUIT + ". Quit.");
    builder.append("\n1. Get number of hotels.");
    builder.append("\n2. Display all hotels.");
    builder.append("\n3. Check availability.");
    builder.append("\n4. Book an offer.");

    System.out.println(builder);
  }

  private void processUserInput(BufferedReader reader, String userInput, RestTemplate proxy) {
    Map<String, String> params = new HashMap<>();
    inputProcessor = new IntegerInputProcessor(reader);

    try {
      switch(userInput) {

        case "1": {
          String uri = URI_HOTELS + "/count";
          String countStr = proxy.getForObject(uri, String.class);
          ObjectMapper mapper = new ObjectMapper();
          long count = (int) mapper.readValue(countStr, Map.class).get("count");
          System.out.println(String.format("There are %d hotels", count));
          System.out.println();
          break;
        }

        case "2": {
          String uri = URI_HOTELS;
          Hotel[] hotels = proxy.getForObject(uri, Hotel[].class);
          System.out.println("Hotels :");
          Arrays.asList(hotels).forEach(System.out::println);
          System.out.println();
          break;
        }

        // ========= WS1 : consulter disponibilités =========
        case "3": {
          System.out.println("=== Check availability ===");

          System.out.print("Start date (yyyy-MM-dd): ");
          LocalDate start = LocalDate.parse(reader.readLine());

          System.out.print("End date (yyyy-MM-dd): ");
          LocalDate end = LocalDate.parse(reader.readLine());

          inputProcessor.setMessage();
          int nbPers = inputProcessor.process();

          AvailabilityRequest req = new AvailabilityRequest();
          // tu peux aussi les demander à l'utilisateur
          req.setAgenceId("AGENCE1");
          req.setPassword("secret");
          req.setDateDebut(start);
          req.setDateFin(end);
          req.setNbPersonnes(nbPers);

          String uri = SERVICE_URL + "/availability";
          AvailabilityOffer[] offers = proxy.postForObject(uri, req, AvailabilityOffer[].class);

          if (offers == null || offers.length == 0) {
            System.out.println("No offers for the given period / persons.");
          } else {
            System.out.println("Available offers:");
            for (AvailabilityOffer o : offers) {
              System.out.println(" - OfferId=" + o.getOfferId()
                      + ", Hotel=" + o.getHotelName()
                      + ", beds=" + o.getNbLits()
                      + ", from " + o.getDateDebut()
                      + " to " + o.getDateFin()
                      + ", price=" + o.getPrix());
            }
          }
          System.out.println();
          break;
        }

        // ========= WS2 : effectuer réservation =========
        case "4": {
          System.out.println("=== Book an offer ===");

          System.out.print("Offer id to book: ");
          Long offerId = Long.parseLong(reader.readLine());

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
          req.setOfferId(offerId);
          req.setPrenom(prenom);
          req.setNom(nom);
          req.setEmail(email);
          req.setTelephone(tel);

          String uri = SERVICE_URL + "/reservations";
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
