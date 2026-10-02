package com.bloodbank.model;
import jakarta.persistence.*;

@Entity
@Table(name = "blood_stock")
public class BloodStock {
    @Id
    private String bloodGroup; // A+, A-, B+, B-, AB+, AB-, O+, O-
    
    private Integer totalUnits;
    
    // Getters and Setters
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public Integer getTotalUnits() { return totalUnits; }
    public void setTotalUnits(Integer totalUnits) { this.totalUnits = totalUnits; }
}
