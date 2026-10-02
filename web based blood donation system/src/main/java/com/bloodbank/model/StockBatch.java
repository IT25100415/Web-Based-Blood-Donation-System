package com.bloodbank.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
@Table(name = "stock_batches")
public class StockBatch {
    @Id
    @NotBlank(message = "Batch Number is required")
    @Pattern(regexp = "^BCH\\d{6}$", message = "Invalid Batch Number")
    private String batchNumber;
    
    @NotBlank(message = "Blood group is required")
    private String bloodGroup;
    
    @NotBlank(message = "Component type is required")
    private String componentType; // Whole Blood, Red Cells, Platelets, Plasma
    
    @NotNull(message = "Collection date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @PastOrPresent(message = "Collection date cannot be in the future")
    private LocalDate collectionDate;
    
    @NotNull(message = "Expiration date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;
    
    private String status; // IN_TESTING, AVAILABLE, RESERVED, QUARANTINED
    
    @NotNull(message = "Units are required")
    @Min(value = 1, message = "At least 1 unit is required")
    private Integer units;
    
    // Getters and Setters
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getComponentType() { return componentType; }
    public void setComponentType(String componentType) { this.componentType = componentType; }
    public LocalDate getCollectionDate() { return collectionDate; }
    public void setCollectionDate(LocalDate collectionDate) { this.collectionDate = collectionDate; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getUnits() { return units; }
    public void setUnits(Integer units) { this.units = units; }
}
