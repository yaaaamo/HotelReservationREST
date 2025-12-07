package com.example.hotel_service.model;

public class ReservationUpdateRequest {

  private String nomClient;
  private String prenomClient;
  private String emailClient;
  private String telephoneClient;

  public String getNomClient() {
    return nomClient;
  }

  public void setNomClient(String nomClient) {
    this.nomClient = nomClient;
  }

  public String getPrenomClient() {
    return prenomClient;
  }

  public void setPrenomClient(String prenomClient) {
    this.prenomClient = prenomClient;
  }

  public String getEmailClient() {
    return emailClient;
  }

  public void setEmailClient(String emailClient) {
    this.emailClient = emailClient;
  }

  public String getTelephoneClient() {
    return telephoneClient;
  }

  public void setTelephoneClient(String telephoneClient) {
    this.telephoneClient = telephoneClient;
  }
}