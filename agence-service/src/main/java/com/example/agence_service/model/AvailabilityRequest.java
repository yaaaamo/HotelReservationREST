package com.example.agence_service.model;
import java.time.LocalDate;

public class AvailabilityRequest {

  private String agenceId;
  private String password;
  private LocalDate dateDebut;
  private LocalDate dateFin;
  private int nbPersonnes;

  public String getAgenceId() { return agenceId; }
  public void setAgenceId(String agenceId) { this.agenceId = agenceId; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public LocalDate getDateDebut() { return dateDebut; }
  public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

  public LocalDate getDateFin() { return dateFin; }
  public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

  public int getNbPersonnes() { return nbPersonnes; }
  public void setNbPersonnes(int nbPersonnes) { this.nbPersonnes = nbPersonnes; }
}

