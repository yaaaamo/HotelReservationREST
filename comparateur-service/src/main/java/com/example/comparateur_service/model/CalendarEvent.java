package com.example.comparateur_service.model;
import java.time.LocalDate;

public class CalendarEvent {

  private String id;
  private String title;
  private String start;
  private String end;
  private String color;
  private String textColor;
  private String borderColor;
  private String description;
  private String url;
  private String reference;
  private String clientName;
  private String hotelName;
  private String chambreNumero;
  private String agenceId;
  private double montant;

  public CalendarEvent() {
    this.textColor = "#ffffff";
  }

  public static CalendarEvent fromReservation(ReservationView res, String color) {
    CalendarEvent event = new CalendarEvent();
    event.setId(res.getReference());
    event.setTitle(res.getHotelNom() + " - " + res.getFullName());
    event.setStart(res.getDateArrivee().toString());
    event.setEnd(res.getDateDepart().plusDays(1).toString()); // FullCalendar end is exclusive
    event.setColor(color);
    event.setBorderColor(color);
    event.setReference(res.getReference());
    event.setClientName(res.getFullName());
    event.setHotelName(res.getHotelNom());
    event.setChambreNumero(res.getChambreNumero());
    event.setAgenceId(res.getAgenceId());
    event.setMontant(res.getMontantTotal());

    String desc = String.format("Client: %s\nHôtel: %s\nChambre: %s (%s)\nAgence: %s\nMontant: %.2f€",
            res.getFullName(),
            res.getHotelNom(),
            res.getChambreNumero(),
            res.getChambreType(),
            res.getAgenceId(),
            res.getMontantTotal());
    event.setDescription(desc);

    return event;
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getStart() { return start; }
  public void setStart(String start) { this.start = start; }

  public String getEnd() { return end; }
  public void setEnd(String end) { this.end = end; }

  public String getColor() { return color; }
  public void setColor(String color) { this.color = color; }

  public String getTextColor() { return textColor; }
  public void setTextColor(String textColor) { this.textColor = textColor; }

  public String getBorderColor() { return borderColor; }
  public void setBorderColor(String borderColor) { this.borderColor = borderColor; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }

  public String getReference() { return reference; }
  public void setReference(String reference) { this.reference = reference; }

  public String getClientName() { return clientName; }
  public void setClientName(String clientName) { this.clientName = clientName; }

  public String getHotelName() { return hotelName; }
  public void setHotelName(String hotelName) { this.hotelName = hotelName; }

  public String getChambreNumero() { return chambreNumero; }
  public void setChambreNumero(String chambreNumero) { this.chambreNumero = chambreNumero; }

  public String getAgenceId() { return agenceId; }
  public void setAgenceId(String agenceId) { this.agenceId = agenceId; }

  public double getMontant() { return montant; }
  public void setMontant(double montant) { this.montant = montant; }
}
