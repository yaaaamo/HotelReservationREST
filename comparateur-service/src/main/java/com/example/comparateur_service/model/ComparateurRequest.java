package com.example.comparateur_service.model;
import java.time.LocalDate;

public class ComparateurRequest {

  private String ville;
  private LocalDate dateDebut;
  private LocalDate dateFin;
  private int nbPersonnes;
  private int nombreEtoilesMin;

  public ComparateurRequest() {}

  public String getVille() { return ville; }
  public void setVille(String ville) { this.ville = ville; }

  public LocalDate getDateDebut() { return dateDebut; }
  public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

  public LocalDate getDateFin() { return dateFin; }
  public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

  public int getNbPersonnes() { return nbPersonnes; }
  public void setNbPersonnes(int nbPersonnes) { this.nbPersonnes = nbPersonnes; }

  public int getNombreEtoilesMin() { return nombreEtoilesMin; }
  public void setNombreEtoilesMin(int nombreEtoilesMin) { this.nombreEtoilesMin = nombreEtoilesMin; }
}
