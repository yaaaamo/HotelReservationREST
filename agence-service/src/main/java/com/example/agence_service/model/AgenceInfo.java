package com.example.agence_service.model;
public class AgenceInfo {
  private String agenceId;
  private String agenceName;

  public AgenceInfo() {}

  public AgenceInfo(String agenceId, String agenceName) {
    this.agenceId = agenceId;
    this.agenceName = agenceName;
  }

  public String getAgenceId() { return agenceId; }
  public void setAgenceId(String agenceId) { this.agenceId = agenceId; }

  public String getAgenceName() { return agenceName; }
  public void setAgenceName(String agenceName) { this.agenceName = agenceName; }
}
