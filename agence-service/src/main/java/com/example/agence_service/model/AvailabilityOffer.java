package com.example.agence_service.model;

import java.time.LocalDate;

public class AvailabilityOffer {

  private String offerId;

  private Long hotelId;
  private String hotelName;
  private int nbLits;
  private LocalDate dateDebut;
  private LocalDate dateFin;
  private double prix;

  public String getOfferId() { return offerId; }
  public void setOfferId(String offerId) { this.offerId = offerId; }

  public Long getHotelId() { return hotelId; }
  public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

  public String getHotelName() { return hotelName; }
  public void setHotelName(String hotelName) { this.hotelName = hotelName; }

  public int getNbLits() { return nbLits; }
  public void setNbLits(int nbLits) { this.nbLits = nbLits; }

  public LocalDate getDateDebut() { return dateDebut; }
  public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

  public LocalDate getDateFin() { return dateFin; }
  public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

  public double getPrix() { return prix; }
  public void setPrix(double prix) { this.prix = prix; }

  @Override
  public String toString() {
    return "Offer{" +
            "offerId='" + offerId + '\'' +
            ", hotelId=" + hotelId +
            ", hotelName='" + hotelName + '\'' +
            ", nbLits=" + nbLits +
            ", dateDebut=" + dateDebut +
            ", dateFin=" + dateFin +
            ", prix=" + prix +
            '}';
  }
}
