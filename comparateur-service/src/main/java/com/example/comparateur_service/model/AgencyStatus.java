package com.example.comparateur_service.model;

public class AgencyStatus {
  private String url;
  private AgenceInfo info;
  private boolean available;

  public AgencyStatus(String url, AgenceInfo info, boolean available) {
    this.url = url;
    this.info = info;
    this.available = available;
  }

  public String getUrl() { return url; }
  public AgenceInfo getInfo() { return info; }
  public boolean isAvailable() { return available; }
}
