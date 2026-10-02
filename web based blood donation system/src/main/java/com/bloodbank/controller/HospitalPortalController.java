package com.bloodbank.controller;

import com.bloodbank.model.*;
import com.bloodbank.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/hospital")
public class HospitalPortalController {

    @Autowired private UserRepository userRepository;
    @Autowired private HospitalRepository hospitalRepository;
    @Autowired private BloodRequestRepository bloodRequestRepository;
    @Autowired private BloodRequestHistoryRepository historyRepository;

    private User getLoggedInUser(Principal principal) {
        return userRepository.findByEmail(principal.getName());
    }

    private Hospital getLoggedInHospital(Principal principal) {
        User user = getLoggedInUser(principal);
        return hospitalRepository.findById(user.getId()).orElse(null);
    }

    // 1. PROFILE MANAGEMENT
    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        Hospital hospital = getLoggedInHospital(principal);
        if (hospital == null) return "redirect:/dashboard";
        
        model.addAttribute("title", "Hospital Profile");
        model.addAttribute("hospital", hospital);
        return "hospital-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam(required = false) String hospitalName,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String emergencyContact,
            @RequestParam(required = false) String representativeName,
            @RequestParam(required = false) String representativePhone,
            @RequestParam(required = false) String registrationNumber,
            Principal principal, RedirectAttributes redirectAttributes) {
            
        Hospital hospital = getLoggedInHospital(principal);
        if (hospital != null) {
            if (hospitalName != null) hospital.setHospitalName(hospitalName);
            if (address != null) hospital.setAddress(address);
            if (emergencyContact != null) hospital.setEmergencyContact(emergencyContact);
            if (representativeName != null) hospital.setRepresentativeName(representativeName);
            if (representativePhone != null) hospital.setRepresentativePhone(representativePhone);
            if (registrationNumber != null) hospital.setRegistrationNumber(registrationNumber);
            hospitalRepository.save(hospital);
            
            redirectAttributes.addFlashAttribute("success", "Hospital details updated successfully!");
        }
        return "redirect:/hospital/profile";
    }

    // 2. BLOOD REQUEST TRACKING
    @GetMapping("/requests")
    public String showRequests(
            @RequestParam(required = false) String statusFilter,
            @RequestParam(required = false) String urgencyFilter,
            @RequestParam(required = false) String bloodGroupFilter,
            Model model, Principal principal) {
            
        Hospital hospital = getLoggedInHospital(principal);
        if (hospital == null) return "redirect:/dashboard";
        
        List<BloodRequest> requests = bloodRequestRepository.findByHospitalIdOrderByRequestedDateDesc(hospital.getId());
        
        // Filtering
        if (statusFilter != null && !statusFilter.isEmpty()) {
            requests = requests.stream().filter(r -> r.getStatus() != null && r.getStatus().name().equals(statusFilter)).collect(Collectors.toList());
        }
        if (urgencyFilter != null && !urgencyFilter.isEmpty()) {
            requests = requests.stream().filter(r -> r.getUrgencyLevel() != null && r.getUrgencyLevel().name().equals(urgencyFilter)).collect(Collectors.toList());
        }
        if (bloodGroupFilter != null && !bloodGroupFilter.isEmpty()) {
            requests = requests.stream().filter(r -> r.getBloodGroup() != null && r.getBloodGroup().equals(bloodGroupFilter)).collect(Collectors.toList());
        }
        
        model.addAttribute("title", "Blood Request Management");
        model.addAttribute("requests", requests);
        
        // Form filters state
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("urgencyFilter", urgencyFilter);
        model.addAttribute("bloodGroupFilter", bloodGroupFilter);
        
        return "hospital-requests";
    }

    // 3. CREATE BLOOD REQUEST
    @PostMapping("/requests/create")
    public String createRequest(@ModelAttribute BloodRequest request, Principal principal, RedirectAttributes redirectAttributes) {
        Hospital hospital = getLoggedInHospital(principal);
        if (hospital == null) return "redirect:/dashboard";
        
        if (request.getUnitsRequired() == null || request.getUnitsRequired() <= 0) {
            redirectAttributes.addFlashAttribute("error", "Invalid quantity. Units required must be at least 1.");
            return "redirect:/hospital/requests";
        }
        
        // Prevent duplicate emergency request for the same patient/group
        if ((request.getUrgencyLevel() == BloodRequest.UrgencyLevel.EMERGENCY || request.getUrgencyLevel() == BloodRequest.UrgencyLevel.CRITICAL) && request.getPatientReference() != null) {
            boolean exists = bloodRequestRepository.existsByHospitalIdAndBloodGroupAndPatientReferenceAndStatusIn(
                    hospital.getId(), request.getBloodGroup(), request.getPatientReference(), 
                    Arrays.asList(BloodRequest.Status.PENDING, BloodRequest.Status.UNDER_REVIEW, BloodRequest.Status.APPROVED));
            if (exists) {
                redirectAttributes.addFlashAttribute("error", "An active emergency request for this patient already exists.");
                return "redirect:/hospital/requests";
            }
        }
        
        // Set up the request
        request.setHospital(hospital);
        request.setRequestedDate(LocalDateTime.now());
        request.setStatus(BloodRequest.Status.PENDING);
        request.setFulfilledQuantity(0);
        
        // Generate Unique Reference (e.g., REQ-YYYYMMDD-HEX)
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String hexPart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        request.setRequestRef("REQ-" + datePart + "-" + hexPart);
        
        BloodRequest savedRequest = bloodRequestRepository.save(request);
        
        // Log history
        saveHistory(savedRequest, savedRequest.getStatus(), getLoggedInUser(principal), "Request created.");
        
        redirectAttributes.addFlashAttribute("success", "Blood request (" + savedRequest.getRequestRef() + ") submitted successfully!");
        return "redirect:/hospital/requests";
    }

    // 4. UPDATE BLOOD REQUEST
    @PostMapping("/requests/{id}/update")
    public String updateRequest(@PathVariable Long id, @RequestParam Integer unitsRequired, @RequestParam BloodRequest.UrgencyLevel urgencyLevel, Principal principal, RedirectAttributes redirectAttributes) {
        Hospital hospital = getLoggedInHospital(principal);
        BloodRequest request = bloodRequestRepository.findById(id).orElse(null);
        
        if (request != null && request.getHospital().getId().equals(hospital.getId())) {
            if (request.getStatus() != BloodRequest.Status.PENDING && request.getStatus() != BloodRequest.Status.UNDER_REVIEW) {
                redirectAttributes.addFlashAttribute("error", "Cannot modify request after it has been approved or processed.");
                return "redirect:/hospital/requests";
            }
            if (unitsRequired <= 0) {
                redirectAttributes.addFlashAttribute("error", "Invalid quantity.");
                return "redirect:/hospital/requests";
            }
            
            boolean changed = false;
            String remarks = "Request updated: ";
            
            if (!request.getUnitsRequired().equals(unitsRequired)) {
                request.setUnitsRequired(unitsRequired);
                changed = true;
                remarks += "Quantity changed to " + unitsRequired + ". ";
            }
            if (request.getUrgencyLevel() != urgencyLevel) {
                request.setUrgencyLevel(urgencyLevel);
                changed = true;
                remarks += "Urgency escalated to " + urgencyLevel + ". ";
            }
            
            if (changed) {
                bloodRequestRepository.save(request);
                saveHistory(request, request.getStatus(), getLoggedInUser(principal), remarks);
                redirectAttributes.addFlashAttribute("success", "Request updated successfully.");
            }
        }
        return "redirect:/hospital/requests";
    }

    // 5. CANCEL BLOOD REQUEST
    @PostMapping("/requests/{id}/cancel")
    public String cancelRequest(@PathVariable Long id, @RequestParam String cancelReason, Principal principal, RedirectAttributes redirectAttributes) {
        Hospital hospital = getLoggedInHospital(principal);
        BloodRequest request = bloodRequestRepository.findById(id).orElse(null);
        
        if (request != null && request.getHospital().getId().equals(hospital.getId())) {
            if (request.getStatus() == BloodRequest.Status.FULFILLED || request.getStatus() == BloodRequest.Status.DISPATCHED) {
                redirectAttributes.addFlashAttribute("error", "Cannot cancel a fulfilled or dispatched request.");
                return "redirect:/hospital/requests";
            }
            
            request.setStatus(BloodRequest.Status.CANCELLED);
            bloodRequestRepository.save(request);
            
            saveHistory(request, request.getStatus(), getLoggedInUser(principal), "Cancelled by hospital: " + cancelReason);
            redirectAttributes.addFlashAttribute("success", "Request cancelled successfully.");
        }
        return "redirect:/hospital/requests";
    }

    // 6. VIEW REQUEST HISTORY
    @GetMapping("/requests/{id}/history")
    public String getRequestHistory(@PathVariable Long id, Model model, Principal principal) {
        Hospital hospital = getLoggedInHospital(principal);
        BloodRequest request = bloodRequestRepository.findById(id).orElse(null);
        
        if (request != null && request.getHospital().getId().equals(hospital.getId())) {
            List<BloodRequestHistory> history = historyRepository.findByBloodRequestIdOrderByTimestampDesc(id);
            model.addAttribute("request", request);
            model.addAttribute("history", history);
            return "fragments/request-history-modal :: historyContent";
        }
        return "error";
    }

    private void saveHistory(BloodRequest request, BloodRequest.Status status, User user, String remarks) {
        BloodRequestHistory history = new BloodRequestHistory();
        history.setBloodRequest(request);
        history.setStatus(status);
        history.setUpdatedBy(user);
        history.setRemarks(remarks);
        history.setTimestamp(LocalDateTime.now());
        historyRepository.save(history);
    }
}
