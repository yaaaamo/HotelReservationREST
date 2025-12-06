package com.example.comparateur_service.web;
import com.example.comparateur_service.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;

@Controller
public class ComparateurWebController {

  @Autowired
  private RestTemplate proxy;

  @Value("${agence1.base-url}")
  private String agence1BaseUrl;

  @Value("${agence2.base-url}")
  private String agence2BaseUrl;

  // Color mapping for agencies
  private static final Map<String, String> AGENCY_COLORS = new HashMap<>();
  static {
    AGENCY_COLORS.put("AGENCE1", "#667eea");  // Purple
    AGENCY_COLORS.put("AGENCE2", "#28a745");  // Green
    AGENCY_COLORS.put("UNKNOWN", "#6c757d");  // Gray
  }

  // Page d'accueil - Formulaire de recherche
  @GetMapping("/")
  public String home(Model model) {
    model.addAttribute("searchRequest", new SearchForm());
    model.addAttribute("today", LocalDate.now());
    return "index";
  }

  // calendar
  @GetMapping("/calendar")
  public String showCalendar(Model model) {
    model.addAttribute("today", LocalDate.now());
    return "calendar";
  }

  // get all reservations
  @GetMapping("/api/calendar/events")
  @ResponseBody
  public List<CalendarEvent> getCalendarEvents(
          @RequestParam(required = false) String start,
          @RequestParam(required = false) String end,
          @RequestParam(required = false) String agencyFilter) {

    List<CalendarEvent> allEvents = new ArrayList<>();
    List<String> agenceUrls = Arrays.asList(agence1BaseUrl, agence2BaseUrl);

    for (String agenceUrl : agenceUrls) {
      try {
        String uri = agenceUrl + "/reservations";
        ReservationView[] reservations = proxy.getForObject(uri, ReservationView[].class);

        if (reservations != null) {
          for (ReservationView res : reservations) {
            // Filter by agency if specified
            if (agencyFilter != null && !agencyFilter.isEmpty()
                    && !agencyFilter.equals(res.getAgenceId())) {
              continue;
            }

            // Filter by date range if specified
            if (start != null && end != null) {
              LocalDate startDate = LocalDate.parse(start.substring(0, 10));
              LocalDate endDate = LocalDate.parse(end.substring(0, 10));

              if (res.getDateDepart().isBefore(startDate) ||
                      res.getDateArrivee().isAfter(endDate)) {
                continue;
              }
            }

            res.setAgenceServiceUrl(agenceUrl);
            String color = AGENCY_COLORS.getOrDefault(res.getAgenceId(), AGENCY_COLORS.get("UNKNOWN"));
            CalendarEvent event = CalendarEvent.fromReservation(res, color);
            allEvents.add(event);
          }
        }
      } catch (Exception e) {
        System.err.println("[WARN] Could not fetch reservations from agency: " + agenceUrl + " - " + e.getMessage());
      }
    }

    return allEvents;
  }


  @GetMapping("/reservations")
  public String showReservationsList(
          @RequestParam(required = false) String agency,
          Model model) {

    List<ReservationView> allReservations = new ArrayList<>();
    List<String> agenceUrls = Arrays.asList(agence1BaseUrl, agence2BaseUrl);
    List<String> errors = new ArrayList<>();

    for (String agenceUrl : agenceUrls) {
      try {
        String uri = agenceUrl + "/reservations";
        ReservationView[] reservations = proxy.getForObject(uri, ReservationView[].class);

        if (reservations != null) {
          for (ReservationView res : reservations) {
            if (agency != null && !agency.isEmpty() && !agency.equals(res.getAgenceId())) {
              continue;
            }
            res.setAgenceServiceUrl(agenceUrl);
            allReservations.add(res);
          }
        }
      } catch (Exception e) {
        errors.add("Could not fetch from agency: " + agenceUrl);
      }
    }

    // Sort by arrival date
    allReservations.sort(Comparator.comparing(ReservationView::getDateArrivee));

    // Calculate statistics
    Map<String, Integer> statsByAgency = new HashMap<>();
    Map<String, Double> revenueByAgency = new HashMap<>();

    for (ReservationView res : allReservations) {
      String agId = res.getAgenceId() != null ? res.getAgenceId() : "UNKNOWN";
      statsByAgency.merge(agId, 1, Integer::sum);
      revenueByAgency.merge(agId, res.getMontantTotal(), Double::sum);
    }

    model.addAttribute("reservations", allReservations);
    model.addAttribute("statsByAgency", statsByAgency);
    model.addAttribute("revenueByAgency", revenueByAgency);
    model.addAttribute("totalReservations", allReservations.size());
    model.addAttribute("totalRevenue", allReservations.stream().mapToDouble(ReservationView::getMontantTotal).sum());
    model.addAttribute("selectedAgency", agency);
    model.addAttribute("errors", errors);
    model.addAttribute("agencyColors", AGENCY_COLORS);

    return "reservations";
  }


  @GetMapping("/reservations/{reference}")
  public String showReservationDetail(@PathVariable String reference, Model model) {
    List<String> agenceUrls = Arrays.asList(agence1BaseUrl, agence2BaseUrl);

    for (String agenceUrl : agenceUrls) {
      try {
        String uri = agenceUrl + "/reservations";
        ReservationView[] reservations = proxy.getForObject(uri, ReservationView[].class);

        if (reservations != null) {
          for (ReservationView res : reservations) {
            if (reference.equals(res.getReference())) {
              res.setAgenceServiceUrl(agenceUrl);

              // HATEOAS
              String selfApiUrl = res.getLink("self");

              model.addAttribute("reservation", res);
              model.addAttribute(
                      "agencyColor",
                      AGENCY_COLORS.getOrDefault(res.getAgenceId(), AGENCY_COLORS.get("UNKNOWN"))
              );
              model.addAttribute("reservationApiUrl", selfApiUrl);

              return "reservation-detail";
            }
          }
        }
      } catch (Exception e) {
        // Continue to next agency
      }
    }

    model.addAttribute("error", "Reservation not found: " + reference);
    return "reservation-detail";
  }




  // Recherche d'offres
  @PostMapping("/search")
  public String search(@ModelAttribute SearchForm form, Model model, HttpSession session) {

    List<AgenceOfferView> allOffers = new ArrayList<>();
    List<String> agenceUrls = Arrays.asList(agence1BaseUrl, agence2BaseUrl);

    ComparateurRequest request = new ComparateurRequest();
    request.setVille(form.getVille() != null && !form.getVille().isEmpty() ? form.getVille() : null);
    request.setDateDebut(form.getDateDebut());
    request.setDateFin(form.getDateFin());
    request.setNbPersonnes(form.getNbPersonnes());
    request.setNombreEtoilesMin(form.getNombreEtoilesMin());

    List<String> errors = new ArrayList<>();

    for (String agenceUrl : agenceUrls) {
      try {
        String uri = agenceUrl + "/offers";
        AgenceOffer[] offers = proxy.postForObject(uri, request, AgenceOffer[].class);

        if (offers != null) {
          for (AgenceOffer offer : offers) {
            allOffers.add(new AgenceOfferView(agenceUrl, offer));
          }
        }
      } catch (HttpClientErrorException e) {
        if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
          errors.add("Erreur avec " + agenceUrl + ": " + e.getStatusCode());
        }
      } catch (Exception e) {
        errors.add("Impossible de contacter: " + agenceUrl);
      }
    }

    // Trier par prix
    allOffers.sort(Comparator.comparingDouble(o -> o.getOffer().getPrix()));

    // Stocker en session pour la réservation
    session.setAttribute("lastOffers", allOffers);
    session.setAttribute("searchForm", form);

    model.addAttribute("offers", allOffers);
    model.addAttribute("searchForm", form);
    model.addAttribute("errors", errors);
    model.addAttribute("today", LocalDate.now());

    return "results";
  }

  // Page de réservation
  @GetMapping("/book/{index}")
  public String showBookingForm(@PathVariable int index, Model model, HttpSession session) {

    @SuppressWarnings("unchecked")
    List<AgenceOfferView> offers = (List<AgenceOfferView>) session.getAttribute("lastOffers");

    if (offers == null || index < 0 || index >= offers.size()) {
      return "redirect:/";
    }

    AgenceOfferView selected = offers.get(index);

    model.addAttribute("offer", selected);
    model.addAttribute("offerIndex", index);
    model.addAttribute("bookingForm", new BookingForm());

    return "booking";
  }

  // Confirmer la réservation
  @PostMapping("/book/{index}")
  public String confirmBooking(@PathVariable int index,
                               @ModelAttribute BookingForm form,
                               Model model,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

    @SuppressWarnings("unchecked")
    List<AgenceOfferView> offers = (List<AgenceOfferView>) session.getAttribute("lastOffers");

    if (offers == null || index < 0 || index >= offers.size()) {
      return "redirect:/";
    }

    AgenceOfferView selected = offers.get(index);
    AgenceOffer offer = selected.getOffer();

    AgenceBookingRequest bookingRequest = new AgenceBookingRequest();
    bookingRequest.setOfferId(offer.getOfferId());
    bookingRequest.setHotelBaseUrl(offer.getHotelBaseUrl());
    bookingRequest.setPrenom(form.getPrenom());
    bookingRequest.setNom(form.getNom());
    bookingRequest.setEmail(form.getEmail());
    bookingRequest.setTelephone(form.getTelephone());
    bookingRequest.setDateDebut(offer.getDateDebut());
    bookingRequest.setDateFin(offer.getDateFin());

    try {
      String uri = selected.getAgenceBaseUrl() + "/reservations";
      BookingResponse response = proxy.postForObject(uri, bookingRequest, BookingResponse.class);

      if (response != null && response.isSuccess()) {
        model.addAttribute("success", true);
        model.addAttribute("reservation", response);
        model.addAttribute("offer", selected);
        model.addAttribute("client", form);
      } else {
        model.addAttribute("success", false);
        model.addAttribute("error", response != null ? response.getMessage() : "Erreur inconnue");
      }
    } catch (Exception e) {
      model.addAttribute("success", false);
      model.addAttribute("error", e.getMessage());
    }

    return "confirmation";
  }

  // Liste des agences
  @GetMapping("/agencies")
  public String listAgencies(Model model) {
    List<AgencyStatus> agencies = new ArrayList<>();
    List<String> agenceUrls = Arrays.asList(agence1BaseUrl, agence2BaseUrl);

    for (String agenceUrl : agenceUrls) {
      try {
        String uri = agenceUrl + "/info";
        AgenceInfo info = proxy.getForObject(uri, AgenceInfo.class);
        agencies.add(new AgencyStatus(agenceUrl, info, true));
      } catch (Exception e) {
        agencies.add(new AgencyStatus(agenceUrl, null, false));
      }
    }

    model.addAttribute("agencies", agencies);
    return "agencies";
  }
}