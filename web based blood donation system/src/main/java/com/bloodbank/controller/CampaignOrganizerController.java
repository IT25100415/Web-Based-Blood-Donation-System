package com.bloodbank.controller;

import com.bloodbank.model.*;
import com.bloodbank.service.CampaignService;
import com.bloodbank.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/organizer")
public class CampaignOrganizerController {

    @Autowired
    private CampaignService campaignService;
    
    @Autowired
    private OrganizerRepository organizerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CampaignSlotRepository slotRepository;
    
    @Autowired
    private CampaignRegistrationRepository registrationRepository;

    private Organizer getLoggedInOrganizer(Principal principal) {
        if (principal == null) return null;
        User user = userRepository.findByEmail(principal.getName());
        return organizerRepository.findById(user.getId()).orElse(null);
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        Organizer organizer = getLoggedInOrganizer(principal);
        if (organizer == null) return "redirect:/login";
        
        List<Campaign> myCampaigns = campaignService.getCampaignsByOrganizer(organizer.getId());
        long activeCount = myCampaigns.stream().filter(c -> "ACTIVE".equals(c.getStatus())).count();
        long completedCount = myCampaigns.stream().filter(c -> "COMPLETED".equals(c.getStatus())).count();
        
        model.addAttribute("title", "Organizer Dashboard");
        model.addAttribute("myCampaigns", myCampaigns);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("completedCount", completedCount);
        return "organizer-dashboard";
    }

    @GetMapping("/campaigns")
    public String viewCampaigns(Model model, Principal principal) {
        Organizer organizer = getLoggedInOrganizer(principal);
        if (organizer == null) return "redirect:/login";
        
        model.addAttribute("title", "My Campaigns");
        model.addAttribute("campaigns", campaignService.getCampaignsByOrganizer(organizer.getId()));
        return "organizer-campaigns";
    }

    @PostMapping("/campaigns/create")
    public String createCampaign(@ModelAttribute Campaign campaign, 
                               @RequestParam(required=false) List<String> slotStart,
                               @RequestParam(required=false) List<String> slotEnd,
                               @RequestParam(required=false) List<Integer> slotCap,
                               Principal principal) {
        Organizer organizer = getLoggedInOrganizer(principal);
        if (organizer == null) return "redirect:/login";
        
        campaign.setOrganizer(organizer);
        
        List<CampaignSlot> slots = new ArrayList<>();
        if (slotStart != null) {
            for (int i=0; i<slotStart.size(); i++) {
                if (!slotStart.get(i).isEmpty() && !slotEnd.get(i).isEmpty() && slotCap.get(i) != null) {
                    CampaignSlot slot = new CampaignSlot();
                    slot.setStartTime(LocalTime.parse(slotStart.get(i)));
                    slot.setEndTime(LocalTime.parse(slotEnd.get(i)));
                    slot.setMaxCapacity(slotCap.get(i));
                    slots.add(slot);
                }
            }
        }
        
        campaignService.createCampaign(campaign, slots);
        return "redirect:/organizer/campaigns?success=created";
    }

    @PostMapping("/campaigns/update")
    public String updateCampaign(@ModelAttribute Campaign campaign,
                               @RequestParam(required=false) List<String> slotStart,
                               @RequestParam(required=false) List<String> slotEnd,
                               @RequestParam(required=false) List<Integer> slotCap,
                               Principal principal) {
        Organizer organizer = getLoggedInOrganizer(principal);
        if (organizer == null) return "redirect:/login";
        
        // Ensure this organizer owns it
        Campaign existing = campaignService.getCampaignById(campaign.getId());
        if (existing == null || !existing.getOrganizer().getId().equals(organizer.getId())) {
            return "redirect:/organizer/campaigns?error=unauthorized";
        }
        
        List<CampaignSlot> slots = new ArrayList<>();
        if (slotStart != null) {
            for (int i=0; i<slotStart.size(); i++) {
                if (!slotStart.get(i).isEmpty() && !slotEnd.get(i).isEmpty() && slotCap.get(i) != null) {
                    CampaignSlot slot = new CampaignSlot();
                    slot.setStartTime(LocalTime.parse(slotStart.get(i)));
                    slot.setEndTime(LocalTime.parse(slotEnd.get(i)));
                    slot.setMaxCapacity(slotCap.get(i));
                    slots.add(slot);
                }
            }
        }
        
        campaignService.updateCampaign(campaign, slots);
        return "redirect:/organizer/campaigns?success=updated";
    }

    @PostMapping("/campaigns/cancel")
    public String cancelCampaign(@RequestParam Long id, @RequestParam String reason, Principal principal) {
        Organizer organizer = getLoggedInOrganizer(principal);
        if (organizer == null) return "redirect:/login";
        
        Campaign existing = campaignService.getCampaignById(id);
        if (existing != null && existing.getOrganizer().getId().equals(organizer.getId())) {
            campaignService.cancelCampaign(id, reason);
        }
        return "redirect:/organizer/campaigns";
    }

    @GetMapping("/campaigns/{id}/registrations")
    public String viewRegistrations(@PathVariable Long id, Model model, Principal principal) {
        Organizer organizer = getLoggedInOrganizer(principal);
        if (organizer == null) return "redirect:/login";
        
        Campaign campaign = campaignService.getCampaignById(id);
        if (campaign == null || !campaign.getOrganizer().getId().equals(organizer.getId())) {
            return "redirect:/organizer/campaigns";
        }
        
        List<CampaignRegistration> registrations = registrationRepository.findByCampaignIdOrderByRegistrationTimeDesc(id);
        
        model.addAttribute("title", "Campaign Registrations");
        model.addAttribute("campaign", campaign);
        model.addAttribute("registrations", registrations);
        return "organizer-registrations";
    }
    
    @PostMapping("/registrations/update-status")
    public String updateRegistrationStatus(@RequestParam Long regId, 
                                         @RequestParam String status, 
                                         @RequestParam(required=false) String reason,
                                         Principal principal) {
        Organizer organizer = getLoggedInOrganizer(principal);
        if (organizer == null) return "redirect:/login";
        
        CampaignRegistration reg = registrationRepository.findById(regId).orElse(null);
        if (reg != null && reg.getCampaign().getOrganizer().getId().equals(organizer.getId())) {
            reg.setAttendanceStatus(status);
            if (reason != null && !reason.isEmpty()) {
                reg.setRejectionReason(reason);
            }
            registrationRepository.save(reg);
            
            // If donated, maybe update campaign collected units
            if ("DONATED".equals(status)) {
                Campaign c = reg.getCampaign();
                c.setCollectedUnits(c.getCollectedUnits() + 1);
                campaignService.updateCampaignStatus(c.getId(), c.getStatus()); // trick to save campaign easily
            }
        }
        return "redirect:/organizer/campaigns/" + (reg != null ? reg.getCampaign().getId() : "") + "/registrations";
    }
}
