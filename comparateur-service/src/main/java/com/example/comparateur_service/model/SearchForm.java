package com.example.comparateur_service.model;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class SearchForm {
  private String ville;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateDebut;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateFin;

  private int nbPersonnes = 1;
  private int nombreEtoilesMin = 0;

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