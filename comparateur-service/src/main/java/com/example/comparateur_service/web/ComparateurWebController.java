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

  // Page d'accueil - Formulaire de recherche
  @GetMapping("/")
  public String home(Model model) {
    model.addAttribute("searchRequest", new SearchForm());
    model.addAttribute("today", LocalDate.now());
    return "index";
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
