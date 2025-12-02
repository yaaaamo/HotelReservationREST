package com.example.hotel_service.model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chambres")
public class Chambre {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String numero;

  @Column(nullable = false)
  private String typeChambre;

  @Column(nullable = false)
  private int nombreLits;

  @Column(nullable = false)
  private double prixParNuit;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id", nullable = false)
  @JsonBackReference
  private Hotel hotel;

  @OneToMany(mappedBy = "chambre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Reservation> reservations = new ArrayList<>();

  public Chambre() {}

  public Chambre(String numero, String typeChambre, int nombreLits, double prixParNuit) {
    this.numero = numero;
    this.typeChambre = typeChambre;
    this.nombreLits = nombreLits;
    this.prixParNuit = prixParNuit;
  }


  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getNumero() { return numero; }
  public void setNumero(String numero) { this.numero = numero; }

  public String getTypeChambre() { return typeChambre; }
  public void setTypeChambre(String typeChambre) { this.typeChambre = typeChambre; }

  public int getNombreLits() { return nombreLits; }
  public void setNombreLits(int nombreLits) { this.nombreLits = nombreLits; }

  public double getPrixParNuit() { return prixParNuit; }
  public void setPrixParNuit(double prixParNuit) { this.prixParNuit = prixParNuit; }

  public Hotel getHotel() { return hotel; }
  public void setHotel(Hotel hotel) { this.hotel = hotel; }

  public List<Reservation> getReservations() { return reservations; }
  public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

  @Override
  public String toString() {
    return String.format("Chambre %s - %s (%d lits) - %.2f€/nuit",
            numero, typeChambre, nombreLits, prixParNuit);
  }
}
