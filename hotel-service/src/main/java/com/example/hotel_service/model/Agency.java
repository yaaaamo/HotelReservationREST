package com.example.hotel_service.model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "agencies")
public class Agency {

  @Id
  private String id;
  private String name;
  private String password;
  private double reductionFactor;

  public Agency() {}

  public Agency(String id, String name, String password, double reductionFactor) {
    this.id = id;
    this.name = name;
    this.password = password;
    this.reductionFactor = reductionFactor;
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public double getReductionFactor() { return reductionFactor; }
  public void setReductionFactor(double reductionFactor) { this.reductionFactor = reductionFactor; }
}
