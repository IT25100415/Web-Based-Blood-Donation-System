package com.bloodbank.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_requests")
public class BloodRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String requestRef;
    
    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;
    
    @NotBlank(message = "Blood group is required")
    private String bloodGroup; // A+, O-, etc.
    
    @NotBlank(message = "Blood component is required")
    private String bloodComponent; // Whole Blood, RBC, Platelets, Plasma
    
    private String patientReference;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @NotNull(message = "Units required is necessary")
    @Min(value = 1, message = "At least 1 unit must be requested")
    private Integer unitsRequired;
    
    private Integer fulfilledQuantity = 0;
    
    public enum UrgencyLevel { NORMAL, URGENT, EMERGENCY, CRITICAL }
    
    @NotNull(message = "Urgency level is required")
    @Enumerated(EnumType.STRING)
    private UrgencyLevel urgencyLevel;
    
    private LocalDateTime requestedDate;
    
    @NotNull(message = "Needed date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Future(message = "Needed date must be in the future")
    private LocalDateTime neededDate;
    
    public enum Status { PENDING, UNDER_REVIEW, APPROVED, PARTIALLY_FULFILLED, FULFILLED, REJECTED, CANCELLED, DISPATCHED }
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRequestRef() { return requestRef; }
    public void setRequestRef(String requestRef) { this.requestRef = requestRef; }
    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getBloodComponent() { return bloodComponent; }
    public void setBloodComponent(String bloodComponent) { this.bloodComponent = bloodComponent; }
    public String getPatientReference() { return patientReference; }
    public void setPatientReference(String patientReference) { this.patientReference = patientReference; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Integer getUnitsRequired() { return unitsRequired; }
    public void setUnitsRequired(Integer unitsRequired) { this.unitsRequired = unitsRequired; }
    public Integer getFulfilledQuantity() { return fulfilledQuantity; }
    public void setFulfilledQuantity(Integer fulfilledQuantity) { this.fulfilledQuantity = fulfilledQuantity; }
    public UrgencyLevel getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(UrgencyLevel urgencyLevel) { this.urgencyLevel = urgencyLevel; }
    public LocalDateTime getRequestedDate() { return requestedDate; }
    public void setRequestedDate(LocalDateTime requestedDate) { this.requestedDate = requestedDate; }
    public LocalDateTime getNeededDate() { return neededDate; }
    public void setNeededDate(LocalDateTime neededDate) { this.neededDate = neededDate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
