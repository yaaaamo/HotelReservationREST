package com.example.hotel_service.model;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "availability_windows")
public class AvailabilityWindow {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate startDate;
  private LocalDate endDate;

  private int quantity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chambre_id", nullable = false)
  private Chambre chambre;

  public AvailabilityWindow() {}

  public AvailabilityWindow(LocalDate startDate, LocalDate endDate, int quantity, Chambre chambre) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.quantity = quantity;
    this.chambre = chambre;
  }

  public Long getId() { return id; }

  public LocalDate getStartDate() { return startDate; }
  public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

  public LocalDate getEndDate() { return endDate; }
  public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

  public int getQuantity() { return quantity; }
  public void setQuantity(int quantity) { this.quantity = quantity; }

  public Chambre getChambre() { return chambre; }
  public void setChambre(Chambre chambre) { this.chambre = chambre; }
}

