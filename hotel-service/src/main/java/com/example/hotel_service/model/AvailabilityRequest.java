package com.example.hotel_service.model;

import java.time.LocalDate;

public class AvailabilityRequest {

  private String agenceId;
  private String password;
  private LocalDate dateDebut;
  private LocalDate dateFin;
  private int nbPersonnes;
  private String ville;
  private int nombreEtoilesMin;

  public int getNombreEtoilesMin() { return nombreEtoilesMin; }
  public void setNombreEtoilesMin(int nombreEtoilesMin) { this.nombreEtoilesMin = nombreEtoilesMin; }

  public String getVille() { return ville; }
  public void setVille(String ville) { this.ville = ville; }

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

