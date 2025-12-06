package com.example.hotel_service.model;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationDTO {

  private Long id;
  private String reference;
  private LocalDate dateArrivee;
  private LocalDate dateDepart;
  private String nomClient;
  private String prenomClient;
  private String emailClient;
  private String telephoneClient;
  private double montantTotal;
  private String statut;
  private String agenceId;
  private LocalDateTime dateReservation;
  private String chambreNumero;
  private String chambreType;
  private String hotelNom;
  private int hotelEtoiles;
  private String hotelVille;

  public ReservationDTO() {}

  public static ReservationDTO fromEntity(Reservation r) {
    ReservationDTO dto = new ReservationDTO();
    dto.setId(r.getId());
    dto.setReference(r.getReference());
    dto.setDateArrivee(r.getDateArrivee());
    dto.setDateDepart(r.getDateDepart());
    dto.setNomClient(r.getNomClient());
    dto.setPrenomClient(r.getPrenomClient());
    dto.setEmailClient(r.getEmailClient());
    dto.setTelephoneClient(r.getTelephoneClient());
    dto.setMontantTotal(r.getMontantTotal());
    dto.setStatut(r.getStatut());
    dto.setAgenceId(r.getAgenceId());
    dto.setDateReservation(r.getDateReservation());

    if (r.getChambre() != null) {
      dto.setChambreNumero(r.getChambre().getNumero());
      dto.setChambreType(r.getChambre().getTypeChambre());
      if (r.getChambre().getHotel() != null) {
        dto.setHotelNom(r.getChambre().getHotel().getNom());
        dto.setHotelEtoiles(r.getChambre().getHotel().getNombreEtoiles());
        dto.setHotelVille(r.getChambre().getHotel().getVille());
      }
    }
    return dto;
  }


  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getReference() { return reference; }
  public void setReference(String reference) { this.reference = reference; }

  public LocalDate getDateArrivee() { return dateArrivee; }
  public void setDateArrivee(LocalDate dateArrivee) { this.dateArrivee = dateArrivee; }

  public LocalDate getDateDepart() { return dateDepart; }
  public void setDateDepart(LocalDate dateDepart) { this.dateDepart = dateDepart; }

  public String getNomClient() { return nomClient; }
  public void setNomClient(String nomClient) { this.nomClient = nomClient; }

  public String getPrenomClient() { return prenomClient; }
  public void setPrenomClient(String prenomClient) { this.prenomClient = prenomClient; }

  public String getEmailClient() { return emailClient; }
  public void setEmailClient(String emailClient) { this.emailClient = emailClient; }

  public String getTelephoneClient() { return telephoneClient; }
  public void setTelephoneClient(String telephoneClient) { this.telephoneClient = telephoneClient; }

  public double getMontantTotal() { return montantTotal; }
  public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }

  public String getStatut() { return statut; }
  public void setStatut(String statut) { this.statut = statut; }

  public String getAgenceId() { return agenceId; }
  public void setAgenceId(String agenceId) { this.agenceId = agenceId; }

  public LocalDateTime getDateReservation() { return dateReservation; }
  public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }

  public String getChambreNumero() { return chambreNumero; }
  public void setChambreNumero(String chambreNumero) { this.chambreNumero = chambreNumero; }

  public String getChambreType() { return chambreType; }
  public void setChambreType(String chambreType) { this.chambreType = chambreType; }

  public String getHotelNom() { return hotelNom; }
  public void setHotelNom(String hotelNom) { this.hotelNom = hotelNom; }

  public int getHotelEtoiles() { return hotelEtoiles; }
  public void setHotelEtoiles(int hotelEtoiles) { this.hotelEtoiles = hotelEtoiles; }

  public String getHotelVille() { return hotelVille; }
  public void setHotelVille(String hotelVille) { this.hotelVille = hotelVille; }
}
