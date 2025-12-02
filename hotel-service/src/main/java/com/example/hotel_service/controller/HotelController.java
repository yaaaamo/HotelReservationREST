package com.example.hotel_service.controller;

import com.example.hotel_service.model.*;
import com.example.hotel_service.repository.ChambreRepository;
import com.example.hotel_service.repository.HotelRepository;
import com.example.hotel_service.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

  private static final String uri = "hotelservice/api";

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

    // 1) Vérifier (éventuellement) les identifiants agence / mot de passe
    // Ici on ne fait que simuler → TODO: vraie vérification plus tard

    LocalDate debut = request.getDateDebut();
    LocalDate fin   = request.getDateFin();
    int nbPers      = request.getNbPersonnes();

    List<AvailabilityOffer> offers = new ArrayList<>();

    // VERSION SIMPLE : on considère toutes les chambres comme dispo
    // (à améliorer en tenant compte des réservations existantes)
    List<Chambre> chambres = chambreRepository.findAll();

    for (Chambre c : chambres) {
      if (c.getNombreLits() >= nbPers) {
        Hotel h = c.getHotel();

        AvailabilityOffer offer = new AvailabilityOffer();
        offer.setOfferId(c.getId());       // offerId = id de la chambre
        offer.setHotelId(h.getId());
        offer.setHotelName(h.getNom());
        offer.setNbLits(c.getNombreLits());
        offer.setDateDebut(debut);
        offer.setDateFin(fin);


        double prixParNuit = c.getPrixParNuit();
        long nbNuits = fin.toEpochDay() - debut.toEpochDay();
        if (nbNuits <= 0) nbNuits = 1;
        offer.setPrix(prixParNuit * nbNuits);

        offers.add(offer);
      }
    }

    return offers;
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(uri + "/reservations")
  public BookingResponse reserver(@RequestBody BookingRequest request) {

    // 1) Vérifier login/mot de passe d’agence si tu as une table des agences
    // Pour l'instant on suppose que c'est ok

    Optional<Chambre> chambreOpt = chambreRepository.findById(request.getOfferId());
    if (!chambreOpt.isPresent()) {
      return new BookingResponse(false,
              "Offre introuvable (chambre inconnue)",
              null);
    }

    Chambre chambre = chambreOpt.get();

    // 2) Créer une réservation
    Reservation res = new Reservation();
    res.setAgenceId(request.getAgenceId());
    res.setNomClient(request.getNom());
    res.setPrenomClient(request.getPrenom());
    res.setEmailClient(request.getEmail());
    res.setTelephoneClient(request.getTelephone());

    // pour les dates, il faudrait normalement les récupérer depuis l’offre;
    // ici on simplifie : on met aujourd’hui + 1 jour
    res.setDateArrivee(LocalDate.now());
    res.setDateDepart(LocalDate.now().plusDays(1));

    // Calcul d’un montant simple
    res.setMontantTotal(chambre.getPrixParNuit());
    res.setChambre(chambre);

    // Génération référence et statut
    res.genererReference();

    reservationRepository.save(res);

    return new BookingResponse(true,
            "Réservation confirmée",
            res.getReference());
  }






}
