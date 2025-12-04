package com.example.agence_service.controller;
import com.example.agence_service.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/agenceservice/api")
public class AgenceController {

  @Autowired
  private RestTemplate proxy;

  @Value("${hotel1.base-url}")
  private String hotel1BaseUrl;

  @Value("${hotel2.base-url}")
  private String hotel2BaseUrl;

  @Value("${agence.id}")
  private String agenceId;

  @Value("${agence.password}")
  private String agencePassword;

  @Value("${agence.name:Agence Inconnue}")
  private String agenceName;


  @PostMapping("/offers")
  public List<AgenceOffer> getOffers(@RequestBody ComparateurRequest request) {

    List<AgenceOffer> agenceOffers = new ArrayList<>();
    List<String> partnerBaseUrls = Arrays.asList(hotel1BaseUrl, hotel2BaseUrl);


    AvailabilityRequest hotelRequest = new AvailabilityRequest();
    hotelRequest.setAgenceId(agenceId);
    hotelRequest.setPassword(agencePassword);
    hotelRequest.setDateDebut(request.getDateDebut());
    hotelRequest.setDateFin(request.getDateFin());
    hotelRequest.setNbPersonnes(request.getNbPersonnes());
    hotelRequest.setVille(request.getVille());
    hotelRequest.setNombreEtoilesMin(request.getNombreEtoilesMin());

    // Interroger chaque hôtel partenaire
    for (String baseUrl : partnerBaseUrls) {
      try {
        String uri = baseUrl + "/availability";
        AvailabilityOffer[] offers = proxy.postForObject(uri, hotelRequest, AvailabilityOffer[].class);

        if (offers != null) {
          for (AvailabilityOffer o : offers) {
            AgenceOffer agenceOffer = new AgenceOffer();
            agenceOffer.setAgenceName(agenceName);
            agenceOffer.setAgenceId(agenceId);
            agenceOffer.setOfferId(o.getOfferId());
            agenceOffer.setHotelName(o.getHotelName());
            agenceOffer.setAdresseHotel(o.getAdresseHotel());
            agenceOffer.setNombreEtoiles(o.getNombreEtoiles());
            agenceOffer.setNbLits(o.getNbLits());
            agenceOffer.setPrix(o.getPrix());
            agenceOffer.setDateDebut(o.getDateDebut());
            agenceOffer.setDateFin(o.getDateFin());
            agenceOffer.setTypeChambre(o.getTypeChambre());
            agenceOffer.setImageUrl(o.getImageUrl());
            agenceOffer.setHotelBaseUrl(baseUrl);

            agenceOffers.add(agenceOffer);
          }
        }
      } catch (HttpClientErrorException e) {
        // Hotel non disponible, on continue
        System.err.println("[WARN] Hotel " + baseUrl + " returned: " + e.getStatusCode());
      } catch (Exception e) {
        System.err.println("[WARN] Could not contact hotel: " + baseUrl);
      }
    }

    return agenceOffers;
  }


  @PostMapping("/reservations")
  @ResponseStatus(HttpStatus.CREATED)
  public BookingResponse makeReservation(@RequestBody AgenceBookingRequest request) {

    // Construire la requête pour l'hôtel
    BookingRequest hotelRequest = new BookingRequest();
    hotelRequest.setAgenceId(agenceId);
    hotelRequest.setPassword(agencePassword);
    hotelRequest.setOfferId(request.getOfferId());
    hotelRequest.setNom(request.getNom());
    hotelRequest.setPrenom(request.getPrenom());
    hotelRequest.setEmail(request.getEmail());
    hotelRequest.setTelephone(request.getTelephone());
    hotelRequest.setDateDebut(request.getDateDebut());
    hotelRequest.setDateFin(request.getDateFin());

    // Envoyer la requête à l'hôtel approprié
    String uri = request.getHotelBaseUrl() + "/reservations";

    try {
      return proxy.postForObject(uri, hotelRequest, BookingResponse.class);
    } catch (Exception e) {
      return new BookingResponse(false, "Erreur lors de la réservation: " + e.getMessage(), null);
    }
  }

   // Info endpoint - pour que le comparateur sache quelle agence c'est
  @GetMapping("/info")
  public AgenceInfo getInfo() {
    return new AgenceInfo(agenceId, agenceName);
  }
}
