package com.example.agence_service.model;


public class BookingResponse {

  private boolean success;
  private String message;
  private String reservationRef;

  public BookingResponse() {}

  public BookingResponse(boolean b, String s, Object o) {
    this.success=b;
    this.message=s;
  }

  public boolean isSuccess() { return success; }
  public void setSuccess(boolean success) { this.success = success; }

  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }

  public String getReservationRef() { return reservationRef; }
  public void setReservationRef(String reservationRef) { this.reservationRef = reservationRef; }

  @Override
  public String toString() {
    return "BookingResponse{" +
            "success=" + success +
            ", message='" + message + '\'' +
            ", reservationRef='" + reservationRef + '\'' +
            '}';
  }
}

