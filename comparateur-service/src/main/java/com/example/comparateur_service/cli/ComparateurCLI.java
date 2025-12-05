package com.example.comparateur_service.cli;

import com.example.comparateur_service.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.*;

@Component
public class ComparateurCLI extends AbstractMain implements CommandLineRunner {

  @Autowired
  private RestTemplate proxy;

  @Value("${agence1.base-url}")
  private String agence1BaseUrl;

  @Value("${agence2.base-url}")
  private String agence2BaseUrl;

  private List<String> agenceBaseUrls;
  private IntegerInputProcessor intProcessor;


  private static class AgenceOfferWithUrl {
    String agenceBaseUrl;
    AgenceOffer offer;

    AgenceOfferWithUrl(String agenceBaseUrl, AgenceOffer offer) {
      this.agenceBaseUrl = agenceBaseUrl;
      this.offer = offer;
    }
  }

  private List<AgenceOfferWithUrl> lastSearchResults = new ArrayList<>();

  @Override
  public void run(String... args) throws Exception {
    BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
    intProcessor = new IntegerInputProcessor(inputReader);
    String userInput = "";

    agenceBaseUrls = Arrays.asList(agence1BaseUrl, agence2BaseUrl);

    System.out.println("BIENVENUE SUR LE COMPARATEUR D'HÔTELS");


    try {
      do {
        menu();
        userInput = inputReader.readLine();
        processUserInput(inputReader, userInput);
        Thread.sleep(2000);
      } while (!userInput.equals(QUIT));

      System.out.println("Merci d'avoir utilisé notre comparateur. Au revoir!");

    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void menu() {
    System.out.println();
    System.out.println("╔════════════════════════════════════╗");
    System.out.println("║           MENU PRINCIPAL           ║");
    System.out.println("╠════════════════════════════════════╣");
    System.out.println("║  1. Rechercher des offres          ║");
    System.out.println("║  2. Réserver une offre             ║");
    System.out.println("║  3. Liste des agences partenaires  ║");
    System.out.println("║  0. Quitter                        ║");
    System.out.println("╚════════════════════════════════════╝");
    System.out.print("Votre choix: ");
  }

  private void processUserInput(BufferedReader reader, String userInput) {
    try {
      switch (userInput) {

        case "1": {
          rechercherOffres(reader);
          break;
        }

        case "2": {
          reserverOffre(reader);
          break;
        }

        case "3": {
          listerAgences();
          break;
        }

        case "0": {
          // Quit
          break;
        }

        default: {
          System.out.println("Option invalide.");
          break;
        }
      }
    } catch (HttpClientErrorException e) {
      System.err.println("Erreur HTTP: " + e.getStatusCode() + " - " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Erreur: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void rechercherOffres(BufferedReader reader) throws IOException {
    System.out.println();
    System.out.println("═══════════════════════════════════════════════════════");
    System.out.println("              RECHERCHE D'OFFRES HÔTELIÈRES             ");
    System.out.println("═══════════════════════════════════════════════════════");


    System.out.print("Ville (ou Entrée pour toutes): ");
    String ville = reader.readLine().trim();

    System.out.print("Nombre d'étoiles minimum (0 pour tous): ");
    int minStars = 0;
    try {
      String starsInput = reader.readLine().trim();
      if (!starsInput.isEmpty()) {
        minStars = Integer.parseInt(starsInput);
      }
    } catch (NumberFormatException e) {
      System.out.println("Nombre invalide, utilisation de 0.");
    }

    System.out.print("Date d'arrivée (yyyy-MM-dd): ");
    LocalDate dateDebut = LocalDate.parse(reader.readLine().trim());

    System.out.print("Date de départ (yyyy-MM-dd): ");
    LocalDate dateFin = LocalDate.parse(reader.readLine().trim());

    intProcessor.setMessage("Nombre de personnes:");
    int nbPersonnes = intProcessor.process();

    // Construire la requête
    ComparateurRequest request = new ComparateurRequest();
    request.setVille(ville.isEmpty() ? null : ville);
    request.setNombreEtoilesMin(minStars);
    request.setDateDebut(dateDebut);
    request.setDateFin(dateFin);
    request.setNbPersonnes(nbPersonnes);

    // Rechercher sur toutes les agences
    lastSearchResults.clear();

    System.out.println();
    System.out.println("Recherche en cours...");

    for (String agenceUrl : agenceBaseUrls) {
      try {
        String uri = agenceUrl + "/offers";
        AgenceOffer[] offers = proxy.postForObject(uri, request, AgenceOffer[].class);

        if (offers != null && offers.length > 0) {
          for (AgenceOffer offer : offers) {
            lastSearchResults.add(new AgenceOfferWithUrl(agenceUrl, offer));
          }
          System.out.println("[OK] " + offers.length + " offre(s) trouvée(s) via " + agenceUrl);
        }
      } catch (HttpClientErrorException e) {
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
          System.out.println("[INFO] Aucune offre disponible via " + agenceUrl);
        } else {
          System.err.println("[WARN] Erreur avec " + agenceUrl + ": " + e.getStatusCode());
        }
      } catch (Exception e) {
        System.err.println("[WARN] Impossible de contacter " + agenceUrl + ": " + e.getMessage());
      }
    }

    // Afficher les résultats
    System.out.println();
    if (lastSearchResults.isEmpty()) {
      System.out.println("╔════════════════════════════════════════════════════════╗");
      System.out.println("║  Aucune offre trouvée pour ces critères.               ║");
      System.out.println("╚════════════════════════════════════════════════════════╝");
    } else {
      // Trier par prix
      lastSearchResults.sort(Comparator.comparingDouble(o -> o.offer.getPrix()));

      System.out.println("╔════════════════════════════════════════════════════════════════════════════════╗");
      System.out.println("║                              RÉSULTATS                                         ║");
      System.out.println("╠════════════════════════════════════════════════════════════════════════════════╣");
      System.out.printf("║  %-3s │ %-20s │ %-15s │ %-6s │ %-5s │ %-10s ║%n",
              "N°", "AGENCE", "HÔTEL", "ÉTOILES", "LITS", "PRIX");
      System.out.println("╠════════════════════════════════════════════════════════════════════════════════╣");

      int idx = 1;
      for (AgenceOfferWithUrl item : lastSearchResults) {
        AgenceOffer o = item.offer;
        System.out.printf("║  %-3d │ %-20s │ %-15s │ %-6s │ %-5d │ %8.2f € ║%n",
                idx,
                truncate(o.getAgenceName(), 20),
                truncate(o.getHotelName(), 15),
                "★".repeat(Math.min(o.getNombreEtoiles(), 5)),
                o.getNbLits(),
                o.getPrix());
        idx++;
      }
      System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");

      System.out.println();
      System.out.println("Total: " + lastSearchResults.size() + " offre(s) trouvée(s)");
    }
  }


  private void reserverOffre(BufferedReader reader) throws IOException {
    System.out.println();
    System.out.println("═══════════════════════════════════════════════════════");
    System.out.println("                   RÉSERVATION                          ");
    System.out.println("═══════════════════════════════════════════════════════");

    if (lastSearchResults.isEmpty()) {
      System.out.println("Aucune offre disponible. Veuillez d'abord effectuer une recherche (option 1).");
      return;
    }

    // Afficher les offres disponibles
    System.out.println("Offres disponibles:");
    int idx = 1;
    for (AgenceOfferWithUrl item : lastSearchResults) {
      AgenceOffer o = item.offer;
      System.out.printf("  %d) [%s] %s - %s - %.2f€%n",
              idx, o.getAgenceName(), o.getHotelName(), o.getOfferId(), o.getPrix());
      idx++;
    }

    // Choisir l'offre
    System.out.println();
    intProcessor.setMessage("Numéro de l'offre à réserver (1-" + lastSearchResults.size() + "):");
    int choix = intProcessor.process();

    if (choix < 1 || choix > lastSearchResults.size()) {
      System.out.println("Numéro invalide.");
      return;
    }

    AgenceOfferWithUrl selected = lastSearchResults.get(choix - 1);
    AgenceOffer offer = selected.offer;

    System.out.println();
    System.out.println("Offre sélectionnée: " + offer.getHotelName() + " via " + offer.getAgenceName());
    System.out.println("Prix: " + offer.getPrix() + "€");
    System.out.println();

    // Collecter les informations client
    System.out.print("Prénom du client: ");
    String prenom = reader.readLine().trim();

    System.out.print("Nom du client: ");
    String nom = reader.readLine().trim();

    System.out.print("Email: ");
    String email = reader.readLine().trim();

    System.out.print("Téléphone: ");
    String telephone = reader.readLine().trim();

    // Construire la requête de réservation
    AgenceBookingRequest bookingRequest = new AgenceBookingRequest();
    bookingRequest.setOfferId(offer.getOfferId());
    bookingRequest.setHotelBaseUrl(offer.getHotelBaseUrl());
    bookingRequest.setPrenom(prenom);
    bookingRequest.setNom(nom);
    bookingRequest.setEmail(email);
    bookingRequest.setTelephone(telephone);
    bookingRequest.setDateDebut(offer.getDateDebut());
    bookingRequest.setDateFin(offer.getDateFin());

    // Envoyer la réservation à l'agence
    String uri = selected.agenceBaseUrl + "/reservations";
    System.out.println();
    System.out.println("Envoi de la réservation...");

    try {
      BookingResponse response = proxy.postForObject(uri, bookingRequest, BookingResponse.class);

      if (response != null && response.isSuccess()) {
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║            RÉSERVATION CONFIRMÉE !                     ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.println("║  Référence: " + response.getReservationRef());
        System.out.println("║  Hôtel: " + offer.getHotelName());
        System.out.println("║  Agence: " + offer.getAgenceName());
        System.out.println("║  Prix: " + offer.getPrix() + "€");
        System.out.println("╚════════════════════════════════════════════════════════╝");
      } else {
        System.out.println("Échec de la réservation: " + (response != null ? response.getMessage() : "Pas de réponse"));
      }
    } catch (Exception e) {
      System.err.println("Erreur lors de la réservation: " + e.getMessage());
    }
  }

  private void listerAgences() {
    System.out.println();
    System.out.println("═══════════════════════════════════════════════════════");
    System.out.println("              AGENCES PARTENAIRES                       ");
    System.out.println("═══════════════════════════════════════════════════════");

    for (String agenceUrl : agenceBaseUrls) {
      try {
        String uri = agenceUrl + "/info";
        AgenceInfo info = proxy.getForObject(uri, AgenceInfo.class);

        if (info != null) {
          System.out.println("  ✓ " + info.getAgenceName() + " (" + info.getAgenceId() + ")");
          System.out.println("    URL: " + agenceUrl);
        }
      } catch (Exception e) {
        System.out.println("  ✗ " + agenceUrl + " (non disponible)");
      }
    }
    System.out.println();
  }


  private String truncate(String str, int maxLength) {
    if (str == null) return "";
    if (str.length() <= maxLength) return str;
    return str.substring(0, maxLength - 3) + "...";
  }
}
