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
import java.util.stream.Collectors;

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


    if (debut == null || fin == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dates cannot be empty");
    }
    if (debut.isAfter(fin)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
    }

    // I removed the "past dates not allowed" for futur executions

    int nbPers = request.getNbPersonnes();
    if (nbPers <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number of persons must be positive");
    }

    double factor = agency.getReductionFactor();

    List<AvailabilityOffer> offers = new ArrayList<>();

    List<AvailabilityWindow> windows =
            availabilityWindowRepository
                    .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(fin, debut);


    if (windows.isEmpty()) {
      return offers;
    }

    String villeRecherche = request.getVille();
    int etoilesMin = request.getNombreEtoilesMin();

    for (AvailabilityWindow win : windows) {
      Chambre c = win.getChambre();
      Hotel h = c.getHotel();

      if (villeRecherche != null && !villeRecherche.isEmpty()) {
        String hotelVille = h.getVille();
        if (hotelVille == null || !hotelVille.equalsIgnoreCase(villeRecherche)) {
          continue;
        }
      }

      if (etoilesMin > 0 && h.getNombreEtoiles() < etoilesMin) {
        continue;
      }

      if (c.getNombreLits() < nbPers) {
        continue;
      }

      LocalDate effStart = debut.isAfter(win.getStartDate()) ? debut : win.getStartDate();
      LocalDate effEnd   = fin.isBefore(win.getEndDate()) ? fin : win.getEndDate();

      if (!effStart.isBefore(effEnd)) {
        continue;
      }
      long reserved = reservationRepository
              .countOverlappingReservations(c, effStart, effEnd);

      int capacity  = win.getQuantity();
      int remaining = capacity - (int) reserved;

      if (remaining <= 0) {
        continue;
      }

      AvailabilityOffer offer = new AvailabilityOffer();
      offer.setHotelId(h.getId());
      offer.setHotelName(h.getNom());
      offer.setNbLits(c.getNombreLits());
      offer.setDateDebut(debut);
      offer.setDateFin(fin);

      long nbNuits = fin.toEpochDay() - debut.toEpochDay();
      if (nbNuits <= 0) nbNuits = 1;

      double prixParNuit = c.getPrixParNuit();
      double basePrice = prixParNuit * nbNuits;
      double finalPrice = basePrice * factor;
      offer.setPrix(finalPrice);

      String roomCode = "R" + c.getNumero();
      String offerId = hotelCode + "-" + roomCode;
      offer.setOfferId(offerId);
      offer.setImageUrl(c.getImageUrl());
      offer.setTypeChambre(c.getTypeChambre());
      offer.setRemaining(remaining);

      String adresse = String.format("%s %s, %s, %s",
              h.getNumero() != null ? h.getNumero() : "",
              h.getRue() != null ? h.getRue() : "",
              h.getVille() != null ? h.getVille() : "",
              h.getPays() != null ? h.getPays() : "");

      offer.setAdresseHotel(adresse);
      offer.setNombreEtoiles(h.getNombreEtoiles());


      offers.add(offer);
    }
    if (offers.isEmpty()) {
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND,
              "No availability for the given dates and number of persons"
      );
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

  @GetMapping(uri + "/reservations")
  public List<ReservationDTO> getAllReservations() {
    List<Reservation> reservations = reservationRepository.findAll();
    return reservations.stream()
            .map(ReservationDTO::fromEntity)
            .collect(Collectors.toList());
  }

  @GetMapping(uri + "/reservations/{id}")
  public ReservationDTO getReservationById(@PathVariable Long id) {
    Reservation res = reservationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return ReservationDTO.fromEntity(res);
  }



  @GetMapping(uri + "/reservations/agency/{agencyId}")
  public List<ReservationDTO> getReservationsByAgency(@PathVariable String agencyId) {
    List<Reservation> reservations = reservationRepository.findByAgenceId(agencyId);
    return reservations.stream()
            .map(ReservationDTO::fromEntity)
            .collect(Collectors.toList());
  }


  @GetMapping(uri + "/reservations/range")
  public List<ReservationDTO> getReservationsByDateRange(
          @RequestParam LocalDate start,
          @RequestParam LocalDate end) {
    List<Reservation> reservations = reservationRepository.findByDateRange(start, end);
    return reservations.stream()
            .map(ReservationDTO::fromEntity)
            .collect(Collectors.toList());
  }
}
