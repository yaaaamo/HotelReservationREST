package com.example.comparateur_service.model;


public class AgenceInfo {

  private String agenceId;
  private String agenceName;

  public AgenceInfo() {}


  public String getAgenceId() { return agenceId; }
  public void setAgenceId(String agenceId) { this.agenceId = agenceId; }

  public String getAgenceName() { return agenceName; }
  public void setAgenceName(String agenceName) { this.agenceName = agenceName; }

  @Override
  public String toString() {
    return agenceName + " (" + agenceId + ")";
  }
}
