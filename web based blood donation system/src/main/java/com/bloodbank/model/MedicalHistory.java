package com.bloodbank.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "medical_history")
public class MedicalHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "donor_id")
    private Donor donor;
    
    private Double hemoglobin;
    private Double weight;
    private String bloodPressure;
    private String diseases;
    private Boolean isEligible;
    private LocalDate nextEligibleDate;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }
    public Double getHemoglobin() { return hemoglobin; }
    public void setHemoglobin(Double hemoglobin) { this.hemoglobin = hemoglobin; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public String getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }
    public String getDiseases() { return diseases; }
    public void setDiseases(String diseases) { this.diseases = diseases; }
    public Boolean getIsEligible() { return isEligible; }
    public void setIsEligible(Boolean eligible) { isEligible = eligible; }
    public LocalDate getNextEligibleDate() { return nextEligibleDate; }
    public void setNextEligibleDate(LocalDate nextEligibleDate) { this.nextEligibleDate = nextEligibleDate; }
}
