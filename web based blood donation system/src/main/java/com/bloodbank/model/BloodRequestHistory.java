package com.bloodbank.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_request_history")
public class BloodRequestHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "request_id")
    private BloodRequest bloodRequest;
    
    @Enumerated(EnumType.STRING)
    private BloodRequest.Status status;
    
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;
    
    @Column(columnDefinition = "TEXT")
    private String remarks;
    
    private LocalDateTime timestamp;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BloodRequest getBloodRequest() { return bloodRequest; }
    public void setBloodRequest(BloodRequest bloodRequest) { this.bloodRequest = bloodRequest; }
    public BloodRequest.Status getStatus() { return status; }
    public void setStatus(BloodRequest.Status status) { this.status = status; }
    public User getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(User updatedBy) { this.updatedBy = updatedBy; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
