package com.example.hotel_service.model;


public class BookingRequest {

  private String agenceId;
  private String login;
  private String password;

  private Long offerId;   // correspondra à l'id de Chambre

  // Infos personne principale
  private String nom;
  private String prenom;
  private String email;
  private String telephone;

  public String getAgenceId() { return agenceId; }
  public void setAgenceId(String agenceId) { this.agenceId = agenceId; }

  public String getLogin() { return login; }
  public void setLogin(String login) { this.login = login; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public Long getOfferId() { return offerId; }
  public void setOfferId(Long offerId) { this.offerId = offerId; }

  public String getNom() { return nom; }
  public void setNom(String nom) { this.nom = nom; }

  public String getPrenom() { return prenom; }
  public void setPrenom(String prenom) { this.prenom = prenom; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getTelephone() { return telephone; }
  public void setTelephone(String telephone) { this.telephone = telephone; }
}

