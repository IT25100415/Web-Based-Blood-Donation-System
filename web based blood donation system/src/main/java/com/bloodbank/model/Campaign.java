package com.bloodbank.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
public class Campaign {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String campaignRef;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;
    
    @NotBlank(message = "Campaign name is required")
    private String campaignName;
    
    @Column(length = 1000)
    private String description;
    
    @NotBlank(message = "Venue is required")
    private String venue;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "Full address is required")
    private String fullAddress;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer targetCapacity;
    
    private Integer targetDonationUnits;
    
    @Column(columnDefinition = "int default 0")
    private Integer collectedUnits = 0;
    
    @NotNull(message = "Start time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;
    
    private String status; // DRAFT, ACTIVE, COMPLETED, CANCELLED, RESCHEDULED
    
    @Column(length = 500)
    private String cancellationReason;
    
    @Column(length = 2000)
    private String outcomeNotes;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCampaignRef() { return campaignRef; }
    public void setCampaignRef(String campaignRef) { this.campaignRef = campaignRef; }
    public Organizer getOrganizer() { return organizer; }
    public void setOrganizer(Organizer organizer) { this.organizer = organizer; }
    public String getCampaignName() { return campaignName; }
    public void setCampaignName(String campaignName) { this.campaignName = campaignName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
    public Integer getTargetCapacity() { return targetCapacity; }
    public void setTargetCapacity(Integer targetCapacity) { this.targetCapacity = targetCapacity; }
    public Integer getTargetDonationUnits() { return targetDonationUnits; }
    public void setTargetDonationUnits(Integer targetDonationUnits) { this.targetDonationUnits = targetDonationUnits; }
    public Integer getCollectedUnits() { return collectedUnits; }
    public void setCollectedUnits(Integer collectedUnits) { this.collectedUnits = collectedUnits; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    public String getOutcomeNotes() { return outcomeNotes; }
    public void setOutcomeNotes(String outcomeNotes) { this.outcomeNotes = outcomeNotes; }
}
