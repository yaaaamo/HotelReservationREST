package com.example.hotel_service.controller;

import com.example.hotel_service.model.*;
import com.example.hotel_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class HotelController {

  @Autowired
  private HotelRepository hotelRepository;

  @Autowired
  private ChambreRepository chambreRepository;

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  private AgencyRepository agencyRepository;

  @Autowired
  private AvailabilityWindowRepository availabilityWindowRepository;


  @Value("${hotel.code}")
  private String hotelCode;

  private static final String uri = "hotelservice/api";

  private boolean overlap(LocalDate start1, LocalDate end1,
                          LocalDate start2, LocalDate end2) {
    // [start1, end1) intersecte [start2, end2) ?
    return !start1.isAfter(end2.minusDays(1)) && !start2.isAfter(end1.minusDays(1));
  }



  @GetMapping(uri+"/hotels")
  public List<Hotel> getAllHotels(){
    return hotelRepository.findAll();
  }

  @GetMapping(uri+"/hotels/count") public String count() {
    return String.format("{\"%s\": %d}", "count", hotelRepository.count());
  }

  @PostMapping(uri + "/availability")
  public List<AvailabilityOffer> consulterDisponibilites(
          @RequestBody AvailabilityRequest request) {

    String agenceId = request.getAgenceId();
    String password = request.getPassword();

    Agency agency = agencyRepository.findById(agenceId)
            .orElse(null);

    if (agency == null || !agency.getPassword().equals(password)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Agence non autorisée");
    }

    LocalDate debut = request.getDateDebut();
    LocalDate fin   = request.getDateFin();
    int nbPers      = request.getNbPersonnes();

    double factor = agency.getReductionFactor();

    List<AvailabilityOffer> offers = new ArrayList<>();


    List<AvailabilityWindow> windows =
            availabilityWindowRepository
                    .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(fin, debut);

    for (AvailabilityWindow win : windows) {
      Chambre c = win.getChambre();


      if (c.getNombreLits() >= nbPers && win.getQuantity() > 0) {
        Hotel h = c.getHotel();

        AvailabilityOffer offer = new AvailabilityOffer();
        offer.setHotelId(h.getId());
        offer.setHotelName(h.getNom());
        offer.setNbLits(c.getNombreLits());

        offer.setDateDebut(debut);
        offer.setDateFin(fin);

        double prixParNuit = c.getPrixParNuit();
        long nbNuits = fin.toEpochDay() - debut.toEpochDay();
        if (nbNuits <= 0) nbNuits = 1;

        double basePrice = prixParNuit * nbNuits;
        double finalPrice = basePrice * factor;
        offer.setPrix(finalPrice);

        String roomCode = "R" + c.getNumero();
        String offerId = hotelCode + "-" + roomCode;
        offer.setOfferId(offerId);
        offer.setImageUrl(c.getImageUrl());
        offer.setTypeChambre(c.getTypeChambre());

        offers.add(offer);
      }
    }

    return offers;
  }




  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(uri + "/reservations")
  public BookingResponse reserver(@RequestBody BookingRequest request) {

    Agency agency = agencyRepository.findById(request.getAgenceId())
            .orElse(null);

    if (agency == null || !agency.getPassword().equals(request.getPassword())) {
      return new BookingResponse(false,
              "Agence non autorisée",
              null);
    }

    String offerId = request.getOfferId();
    String[] parts = offerId.split("-R");
    if (parts.length != 2) {
      return new BookingResponse(false,
              "Format d'identifiant d'offre invalide: " + offerId,
              null);
    }

    String roomNumero = parts[1];

    Optional<Chambre> chambreOpt = chambreRepository.findByNumero(roomNumero);
    if (!chambreOpt.isPresent()) {
      return new BookingResponse(false,
              "Offre introuvable pour la chambre " + roomNumero,
              null);
    }

    Chambre chambre = chambreOpt.get();


    LocalDate debut = request.getDateDebut();
    LocalDate fin   = request.getDateFin();
    long nbNuits = fin.toEpochDay() - debut.toEpochDay();
    if (nbNuits <= 0) nbNuits = 1;

    double basePrice = chambre.getPrixParNuit() * nbNuits;
    double factor    = agency.getReductionFactor();
    double finalPrice = basePrice * factor;

    Reservation res = new Reservation();
    res.setAgenceId(request.getAgenceId());
    res.setNomClient(request.getNom());
    res.setPrenomClient(request.getPrenom());
    res.setEmailClient(request.getEmail());
    res.setTelephoneClient(request.getTelephone());

    res.setDateArrivee(debut);
    res.setDateDepart(fin);
    res.setMontantTotal(finalPrice);
    res.setChambre(chambre);
    res.genererReference();

    reservationRepository.save(res);

    return new BookingResponse(true,
            "Réservation confirmée",
            res.getReference());
  }
}
