package com.example.comparateur_service.model;

public class AgenceOfferView {
  private String agenceBaseUrl;
  private AgenceOffer offer;

  public AgenceOfferView(String agenceBaseUrl, AgenceOffer offer) {
    this.agenceBaseUrl = agenceBaseUrl;
    this.offer = offer;
  }

  public String getAgenceBaseUrl() { return agenceBaseUrl; }
  public AgenceOffer getOffer() { return offer; }
}

