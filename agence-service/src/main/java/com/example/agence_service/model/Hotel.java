package com.example.agence_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Hotel {

  private Long id;
  private String nom;
  private int nombreEtoiles;
  private String pays;
  private String ville;
  private String rue;
  private String numero;
  private String lieuDit;
  private Double latitude;
  private Double longitude;

  public Hotel() {}

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getNom() { return nom; }
  public void setNom(String nom) { this.nom = nom; }

  public int getNombreEtoiles() { return nombreEtoiles; }
  public void setNombreEtoiles(int nombreEtoiles) { this.nombreEtoiles = nombreEtoiles; }

  public String getPays() { return pays; }
  public void setPays(String pays) { this.pays = pays; }

  public String getVille() { return ville; }
  public void setVille(String ville) { this.ville = ville; }

  public String getRue() { return rue; }
  public void setRue(String rue) { this.rue = rue; }

  public String getNumero() { return numero; }
  public void setNumero(String numero) { this.numero = numero; }

  public String getLieuDit() { return lieuDit; }
  public void setLieuDit(String lieuDit) { this.lieuDit = lieuDit; }

  public Double getLatitude() { return latitude; }
  public void setLatitude(Double latitude) { this.latitude = latitude; }

  public Double getLongitude() { return longitude; }
  public void setLongitude(Double longitude) { this.longitude = longitude; }

  @Override
  public String toString() {
    return "Hotel{" +
            "id=" + id +
            ", nom='" + nom + '\'' +
            ", nombreEtoiles=" + nombreEtoiles +
            ", ville='" + ville + '\'' +
            ", pays='" + pays + '\'' +
            '}';
  }
}

