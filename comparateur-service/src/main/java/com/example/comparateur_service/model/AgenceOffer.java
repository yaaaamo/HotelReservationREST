package com.example.comparateur_service.model;

import java.time.LocalDate;

public class AgenceOffer {

  private String agenceName;
  private String agenceId;
  private String offerId;
  private String hotelName;
  private String adresseHotel;
  private int nombreEtoiles;
  private int nbLits;
  private double prix;
  private LocalDate dateDebut;
  private LocalDate dateFin;
  private String typeChambre;
  private String imageUrl;
  private String hotelBaseUrl;

  public AgenceOffer() {}


  public String getAgenceName() { return agenceName; }
  public void setAgenceName(String agenceName) { this.agenceName = agenceName; }

  public String getAgenceId() { return agenceId; }
  public void setAgenceId(String agenceId) { this.agenceId = agenceId; }

  public String getOfferId() { return offerId; }
  public void setOfferId(String offerId) { this.offerId = offerId; }

  public String getHotelName() { return hotelName; }
  public void setHotelName(String hotelName) { this.hotelName = hotelName; }

  public String getAdresseHotel() { return adresseHotel; }
  public void setAdresseHotel(String adresseHotel) { this.adresseHotel = adresseHotel; }

  public int getNombreEtoiles() { return nombreEtoiles; }
  public void setNombreEtoiles(int nombreEtoiles) { this.nombreEtoiles = nombreEtoiles; }

  public int getNbLits() { return nbLits; }
  public void setNbLits(int nbLits) { this.nbLits = nbLits; }

  public double getPrix() { return prix; }
  public void setPrix(double prix) { this.prix = prix; }

  public LocalDate getDateDebut() { return dateDebut; }
  public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

  public LocalDate getDateFin() { return dateFin; }
  public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

  public String getTypeChambre() { return typeChambre; }
  public void setTypeChambre(String typeChambre) { this.typeChambre = typeChambre; }

  public String getImageUrl() { return imageUrl; }
  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

  public String getHotelBaseUrl() { return hotelBaseUrl; }
  public void setHotelBaseUrl(String hotelBaseUrl) { this.hotelBaseUrl = hotelBaseUrl; }

  @Override
  public String toString() {
    return String.format("[%s] %s (%d★) - %s - %d lits - %.2f€",
            agenceName, hotelName, nombreEtoiles, adresseHotel, nbLits, prix);
  }
}
