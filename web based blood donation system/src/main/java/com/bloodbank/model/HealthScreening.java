package com.bloodbank.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_screenings")
public class HealthScreening {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "donor_id")
    private Donor donor;
    
    private LocalDateTime requestDate;
    private LocalDateTime scheduledDate;
    
    @Enumerated(EnumType.STRING)
    private ScreeningStatus status; // PENDING, APPROVED, COMPLETED, CANCELLED
    
    public enum ScreeningStatus { PENDING, APPROVED, COMPLETED, CANCELLED }
    
    private String requestedTests;
    
    // Results: POSITIVE/NEGATIVE or values
    private String hepB;
    private String hepC;
    private String hiv;
    private String dengue;
    private String bloodGroupConfirmed;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }
    
    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }
    
    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }
    
    public ScreeningStatus getStatus() { return status; }
    public void setStatus(ScreeningStatus status) { this.status = status; }
    
    public String getRequestedTests() { return requestedTests; }
    public void setRequestedTests(String requestedTests) { this.requestedTests = requestedTests; }
    
    public String getHepB() { return hepB; }
    public void setHepB(String hepB) { this.hepB = hepB; }
    
    public String getHepC() { return hepC; }
    public void setHepC(String hepC) { this.hepC = hepC; }
    
    public String getHiv() { return hiv; }
    public void setHiv(String hiv) { this.hiv = hiv; }
    
    public String getDengue() { return dengue; }
    public void setDengue(String dengue) { this.dengue = dengue; }
    
    public String getBloodGroupConfirmed() { return bloodGroupConfirmed; }
    public void setBloodGroupConfirmed(String bloodGroupConfirmed) { this.bloodGroupConfirmed = bloodGroupConfirmed; }
}
