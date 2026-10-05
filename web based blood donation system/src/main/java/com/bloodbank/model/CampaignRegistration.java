package com.bloodbank.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaign_registrations")
public class CampaignRegistration {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;
    
    @ManyToOne
    @JoinColumn(name = "donor_id")
    private Donor donor;
    
    @ManyToOne
    @JoinColumn(name = "slot_id")
    private CampaignSlot slot;
    
    private LocalDateTime registrationTime;
    
    // REGISTERED, ATTENDED, ABSENT, REJECTED_AT_SCREENING, DONATED
    private String attendanceStatus; 
    
    private String eligibilityStatus; // ELIGIBLE, INELIGIBLE, PENDING
    
    @Column(length = 500)
    private String rejectionReason;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Campaign getCampaign() { return campaign; }
    public void setCampaign(Campaign campaign) { this.campaign = campaign; }
    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }
    public CampaignSlot getSlot() { return slot; }
    public void setSlot(CampaignSlot slot) { this.slot = slot; }
    public LocalDateTime getRegistrationTime() { return registrationTime; }
    public void setRegistrationTime(LocalDateTime registrationTime) { this.registrationTime = registrationTime; }
    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }
    public String getEligibilityStatus() { return eligibilityStatus; }
    public void setEligibilityStatus(String eligibilityStatus) { this.eligibilityStatus = eligibilityStatus; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
