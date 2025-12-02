package com.example.agence_service.model;

import java.time.LocalDate;

public class BookingRequest {

  private String agenceId;
  private String login;
  private String password;

  private String offerId;

  private String nom;
  private String prenom;
  private String email;
  private String telephone;
  private LocalDate dateDebut;
  private LocalDate dateFin;

  public LocalDate getDateDebut() {
    return dateDebut;
  }

  public void setDateDebut(LocalDate dateDebut) {
    this.dateDebut = dateDebut;
  }

  public LocalDate getDateFin() {
    return dateFin;
  }

  public void setDateFin(LocalDate dateFin) {
    this.dateFin = dateFin;
  }

  public String getAgenceId() { return agenceId; }
  public void setAgenceId(String agenceId) { this.agenceId = agenceId; }

  public String getLogin() { return login; }
  public void setLogin(String login) { this.login = login; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public String getOfferId() { return offerId; }
  public void setOfferId(String offerId) { this.offerId = offerId; }

  public String getNom() { return nom; }
  public void setNom(String nom) { this.nom = nom; }

  public String getPrenom() { return prenom; }
  public void setPrenom(String prenom) { this.prenom = prenom; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getTelephone() { return telephone; }
  public void setTelephone(String telephone) { this.telephone = telephone; }
}
