package com.bloodbank.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "donors")
public class Donor {
    @Id
    private Long id;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;
    
    @NotBlank(message = "NIC is required")
    @Pattern(regexp = "^([0-9]{9}[vVxX]|[0-9]{12})$", message = "Invalid Sri Lankan NIC")
    @Column(unique = true, nullable = false)
    private String nic;
    
    @NotBlank(message = "Blood group is required")
    private String bloodGroup;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(0\\d{9})$", message = "Phone must be 10 digits starting with 0")
    private String phone;
    
    // New Fields for Profile Management
    private String address;
    private LocalDate dateOfBirth;
    private String gender;
    private Double weight;
    private LocalDate lastDonationDate;
    
    @Column(name = "is_active", nullable = false, columnDefinition = "bit default 1")
    private Boolean isActive = true;
    
    private String privacySettings; // e.g., PUBLIC, PRIVATE
    private String notificationPreferences; // e.g., EMAIL, SMS, BOTH, NONE
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public LocalDate getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(LocalDate lastDonationDate) { this.lastDonationDate = lastDonationDate; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public String getPrivacySettings() { return privacySettings; }
    public void setPrivacySettings(String privacySettings) { this.privacySettings = privacySettings; }
    public String getNotificationPreferences() { return notificationPreferences; }
    public void setNotificationPreferences(String notificationPreferences) { this.notificationPreferences = notificationPreferences; }
}

