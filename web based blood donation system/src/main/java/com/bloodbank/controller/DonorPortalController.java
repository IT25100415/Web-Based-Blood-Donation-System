package com.bloodbank.controller;

import com.bloodbank.model.*;
import com.bloodbank.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/donor")
public class DonorPortalController {

    @Autowired private UserRepository userRepository;
    @Autowired private DonorRepository donorRepository;
    @Autowired private EmergencyContactRepository emergencyContactRepository;
    @Autowired private DonationHistoryRepository donationHistoryRepository;
    @Autowired private CampaignRepository campaignRepository;
    @Autowired private CampaignRegistrationRepository registrationRepository;

    private User getLoggedInUser(Principal principal) {
        return userRepository.findByEmail(principal.getName());
    }

    private Donor getLoggedInDonor(Principal principal) {
        User user = getLoggedInUser(principal);
        return donorRepository.findById(user.getId()).orElse(null);
    }

    // 1. PROFILE MANAGEMENT
    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        Donor donor = getLoggedInDonor(principal);
        if (donor == null) return "redirect:/dashboard";
        
        EmergencyContact contact = emergencyContactRepository.findByDonorId(donor.getId());
        if (contact == null) {
            contact = new EmergencyContact();
            contact.setDonor(donor);
        }
        
        model.addAttribute("title", "My Profile");
        model.addAttribute("donor", donor);
        model.addAttribute("contact", contact);
        
        // Calculate Eligibility
        LocalDate nextEligible = donor.getLastDonationDate() != null ? donor.getLastDonationDate().plusDays(90) : LocalDate.now();
        boolean isEligible = LocalDate.now().isAfter(nextEligible) || LocalDate.now().isEqual(nextEligible);
        
        model.addAttribute("nextEligible", nextEligible);
        model.addAttribute("isEligible", isEligible);
        
        return "donor-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) String donorPhone,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) LocalDate dateOfBirth,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Double weight,
            @RequestParam(required = false) String contactName,
            @RequestParam(required = false) String relationship,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) String privacySettings,
            @RequestParam(required = false) String notificationPreferences,
            Principal principal, RedirectAttributes redirectAttributes) {
            
        Donor donor = getLoggedInDonor(principal);
        if (donor != null) {
            if (weight != null && (weight <= 0 || weight > 200)) {
                redirectAttributes.addFlashAttribute("error", "Invalid weight. Please enter a valid weight (Max 200 kg).");
                return "redirect:/donor/profile";
            }
            if (bloodGroup != null) donor.setBloodGroup(bloodGroup);
            if (donorPhone != null) donor.setPhone(donorPhone);
            if (city != null) donor.setCity(city);
            if (address != null) donor.setAddress(address);
            if (dateOfBirth != null) donor.setDateOfBirth(dateOfBirth);
            if (gender != null) donor.setGender(gender);
            if (weight != null) donor.setWeight(weight);
            if (privacySettings != null) donor.setPrivacySettings(privacySettings);
            if (notificationPreferences != null) donor.setNotificationPreferences(notificationPreferences);
            donorRepository.save(donor);
            
            EmergencyContact contact = emergencyContactRepository.findByDonorId(donor.getId());
            if (contact == null) {
                contact = new EmergencyContact();
                contact.setDonor(donor);
            }
            if (contactName != null) contact.setContactName(contactName);
            if (relationship != null) contact.setRelationship(relationship);
            if (contactPhone != null) contact.setPhone(contactPhone);
            emergencyContactRepository.save(contact);
            
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        }
        return "redirect:/donor/profile";
    }

    @PostMapping("/profile/deactivate")
    public String deactivateProfile(Principal principal) {
        Donor donor = getLoggedInDonor(principal);
        if (donor != null) {
            donor.setIsActive(false);
            donorRepository.save(donor);
        }
        return "redirect:/logout";
    }

    // 2. DONATION HISTORY & CERTIFICATES
    @GetMapping("/history")
    public String showHistory(Model model, Principal principal) {
        Donor donor = getLoggedInDonor(principal);
        if (donor == null) return "redirect:/dashboard";
        
        List<DonationHistory> history = donationHistoryRepository.findByDonorIdOrderByDonationDateDesc(donor.getId());
        
        model.addAttribute("title", "Donation History");
        model.addAttribute("history", history);
        return "donor-history";
    }

    @GetMapping("/certificate/{id}")
    public String viewCertificate(@PathVariable Long id, Model model, Principal principal) {
        Donor donor = getLoggedInDonor(principal);
        DonationHistory donation = donationHistoryRepository.findById(id).orElse(null);
        
        if (donation != null && donor != null && donation.getDonor().getId().equals(donor.getId())) {
            model.addAttribute("donation", donation);
            model.addAttribute("donor", donor);
            return "donor-certificate"; // Separate print-friendly layout
        }
        return "redirect:/donor/history";
    }

    // 3. CAMPAIGN REGISTRATION
    @GetMapping("/campaigns")
    public String showCampaigns(Model model, Principal principal) {
        Donor donor = getLoggedInDonor(principal);
        if (donor == null) return "redirect:/dashboard";
        
        List<Campaign> activeCampaigns = campaignRepository.findByStatusInOrderByStartTimeAsc(Arrays.asList("SCHEDULED", "ACTIVE"));
        List<CampaignRegistration> myRegistrations = registrationRepository.findByDonorIdOrderByRegistrationTimeDesc(donor.getId());
        
        // Compute available slots
        Map<Long, Integer> availableSlots = new HashMap<>();
        for (Campaign c : activeCampaigns) {
            int registered = registrationRepository.countByCampaignId(c.getId());
            int available = c.getTargetCapacity() != null ? c.getTargetCapacity() - registered : 0;
            availableSlots.put(c.getId(), available);
        }
        
        model.addAttribute("title", "Donation Campaigns");
        model.addAttribute("activeCampaigns", activeCampaigns);
        model.addAttribute("myRegistrations", myRegistrations);
        model.addAttribute("availableSlots", availableSlots);
        model.addAttribute("donorId", donor.getId());
        
        return "donor-campaigns";
    }

    @PostMapping("/campaigns/register")
    public String registerCampaign(@RequestParam Long campaignId, Principal principal, RedirectAttributes redirectAttributes) {
        Donor donor = getLoggedInDonor(principal);
        Campaign campaign = campaignRepository.findById(campaignId).orElse(null);
        
        if (donor != null && campaign != null) {
            // Check duplicates
            if (registrationRepository.existsByCampaignIdAndDonorId(campaign.getId(), donor.getId())) {
                redirectAttributes.addFlashAttribute("error", "You are already registered for this campaign.");
                return "redirect:/donor/campaigns";
            }
            // Check capacity
            int registered = registrationRepository.countByCampaignId(campaign.getId());
            if (campaign.getTargetCapacity() != null && registered >= campaign.getTargetCapacity()) {
                redirectAttributes.addFlashAttribute("error", "Campaign is full.");
                return "redirect:/donor/campaigns";
            }
            
            CampaignRegistration reg = new CampaignRegistration();
            reg.setCampaign(campaign);
            reg.setDonor(donor);
            reg.setRegistrationTime(LocalDateTime.now());
            reg.setAttendanceStatus("REGISTERED");
            registrationRepository.save(reg);
            
            redirectAttributes.addFlashAttribute("success", "Successfully registered for the campaign!");
        }
        return "redirect:/donor/campaigns";
    }

    @PostMapping("/campaigns/cancel")
    public String cancelRegistration(@RequestParam Long registrationId, Principal principal, RedirectAttributes redirectAttributes) {
        Donor donor = getLoggedInDonor(principal);
        CampaignRegistration reg = registrationRepository.findById(registrationId).orElse(null);
        
        if (reg != null && donor != null && reg.getDonor().getId().equals(donor.getId())) {
            registrationRepository.delete(reg);
            redirectAttributes.addFlashAttribute("success", "Registration cancelled successfully.");
        }
        return "redirect:/donor/campaigns";
    }
}
