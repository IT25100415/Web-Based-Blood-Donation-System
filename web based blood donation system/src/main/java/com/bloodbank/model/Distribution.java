package com.bloodbank.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "distributions")
public class Distribution {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "request_id")
    private BloodRequest bloodRequest;
    
    @NotBlank(message = "Driver name is required")
    private String driverName;
    
    @NotBlank(message = "Vehicle registration is required")
    @Pattern(regexp = "^[A-Za-z]{2,3}[\\s-]\\d{4}$", message = "Invalid SL Vehicle Reg format")
    private String vehicleRegistration;
    
    @NotBlank(message = "Route details are required")
    private String routeDetails;
    
    private Double currentTemperature;
    private String gpsCoordinates;
    private LocalDateTime dispatchTime;
    private String status; // PREPARING, IN_TRANSIT, DELIVERED
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BloodRequest getBloodRequest() { return bloodRequest; }
    public void setBloodRequest(BloodRequest bloodRequest) { this.bloodRequest = bloodRequest; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public String getVehicleRegistration() { return vehicleRegistration; }
    public void setVehicleRegistration(String vehicleRegistration) { this.vehicleRegistration = vehicleRegistration; }
    public String getRouteDetails() { return routeDetails; }
    public void setRouteDetails(String routeDetails) { this.routeDetails = routeDetails; }
    public Double getCurrentTemperature() { return currentTemperature; }
    public void setCurrentTemperature(Double currentTemperature) { this.currentTemperature = currentTemperature; }
    public String getGpsCoordinates() { return gpsCoordinates; }
    public void setGpsCoordinates(String gpsCoordinates) { this.gpsCoordinates = gpsCoordinates; }
    public LocalDateTime getDispatchTime() { return dispatchTime; }
    public void setDispatchTime(LocalDateTime dispatchTime) { this.dispatchTime = dispatchTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

