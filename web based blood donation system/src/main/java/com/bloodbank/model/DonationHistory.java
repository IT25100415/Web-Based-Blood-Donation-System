package com.bloodbank.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "donation_history")
public class DonationHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "donor_id")
    private Donor donor;
    
    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;
    
    private LocalDate donationDate;
    private String bloodGroup;
    private String batchNumber; // Reference to stock_batches
    
    // New Fields
    @Column(unique = true)
    private String donationRef; // e.g. REF-2026-XXXX
    private String status; // SUCCESSFUL, FAILED
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }
    public Campaign getCampaign() { return campaign; }
    public void setCampaign(Campaign campaign) { this.campaign = campaign; }
    public LocalDate getDonationDate() { return donationDate; }
    public void setDonationDate(LocalDate donationDate) { this.donationDate = donationDate; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    
    public String getDonationRef() { return donationRef; }
    public void setDonationRef(String donationRef) { this.donationRef = donationRef; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
