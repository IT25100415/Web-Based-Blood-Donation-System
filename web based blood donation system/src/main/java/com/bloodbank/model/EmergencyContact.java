package com.bloodbank.model;
import jakarta.persistence.*;

@Entity
@Table(name = "emergency_contacts")
public class EmergencyContact {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "donor_id")
    private Donor donor;
    
    private String contactName;
    private String relationship;
    private String phone;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
