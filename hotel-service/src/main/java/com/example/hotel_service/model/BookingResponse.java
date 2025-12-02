package com.example.hotel_service.model;



public class BookingResponse {

  private boolean success;
  private String message;
  private String reservationRef;  // null si pas confirmée

  public BookingResponse() {}

  public BookingResponse(boolean success, String message, String reservationRef) {
    this.success = success;
    this.message = message;
    this.reservationRef = reservationRef;
  }

  public boolean isSuccess() { return success; }
  public void setSuccess(boolean success) { this.success = success; }

  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }

  public String getReservationRef() { return reservationRef; }
  public void setReservationRef(String reservationRef) { this.reservationRef = reservationRef; }
}

