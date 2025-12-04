package com.example.agence_service.model;

import java.time.LocalDate;

public class AgenceBookingRequest {
  private String offerId;
  private String hotelBaseUrl;
  private String nom;
  private String prenom;
  private String email;
  private String telephone;
  private LocalDate dateDebut;
  private LocalDate dateFin;

  public String getOfferId() { return offerId; }
  public void setOfferId(String offerId) { this.offerId = offerId; }

  public String getHotelBaseUrl() { return hotelBaseUrl; }
  public void setHotelBaseUrl(String hotelBaseUrl) { this.hotelBaseUrl = hotelBaseUrl; }

  public String getNom() { return nom; }
  public void setNom(String nom) { this.nom = nom; }

  public String getPrenom() { return prenom; }
  public void setPrenom(String prenom) { this.prenom = prenom; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getTelephone() { return telephone; }
  public void setTelephone(String telephone) { this.telephone = telephone; }

  public LocalDate getDateDebut() { return dateDebut; }
  public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

  public LocalDate getDateFin() { return dateFin; }
  public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
}
