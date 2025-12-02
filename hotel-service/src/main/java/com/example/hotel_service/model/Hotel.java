package com.example.hotel_service.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotels")
public class Hotel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String nom;

  @Column(nullable = false)
  private int nombreEtoiles;

  // Adresse
  private String pays;
  private String ville;
  private String rue;
  private String numero;
  private String lieuDit;
  private Double latitude;
  private Double longitude;

  @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Chambre> chambres = new ArrayList<>();

  public Hotel() {}

  public Hotel(String nom, int nombreEtoiles, String pays, String ville,
               String rue, String numero, Double latitude, Double longitude) {
    this.nom = nom;
    this.nombreEtoiles = nombreEtoiles;
    this.pays = pays;
    this.ville = ville;
    this.rue = rue;
    this.numero = numero;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  // Getters et Setters
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

  public List<Chambre> getChambres() { return chambres; }
  public void setChambres(List<Chambre> chambres) { this.chambres = chambres; }

  public void addChambre(Chambre chambre) {
    chambres.add(chambre);
    chambre.setHotel(this);
  }
}
