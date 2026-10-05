package com.bloodbank.controller;

import com.bloodbank.model.Campaign;
import com.bloodbank.service.CampaignService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import com.bloodbank.repository.CampaignRegistrationRepository;
import com.bloodbank.model.CampaignRegistration;
import java.util.List;

@Controller
@RequestMapping("/campaigns")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

        @Autowired
    private CampaignRegistrationRepository registrationRepository;

    @GetMapping("/{id}/donors")
    public String viewRegisteredDonors(@PathVariable Long id, Model model) {
        Campaign campaign = campaignService.getAllCampaigns().stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
        if (campaign == null) return "redirect:/campaigns";
        
        List<CampaignRegistration> registrations = registrationRepository.findAll().stream()
            .filter(r -> r.getCampaign() != null && r.getCampaign().getId().equals(id))
            .toList();
            
        model.addAttribute("title", "Registered Donors");
        model.addAttribute("campaign", campaign);
        model.addAttribute("registrations", registrations);
        return "campaign-donors";
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("title", "Donation Drives");
        model.addAttribute("campaigns", campaignService.getAllCampaigns());
        return "campaigns";
    }

    @PostMapping("/create")
    public String createCampaign(@Valid @ModelAttribute Campaign campaign, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "redirect:/campaigns?error=validation";
        }
        
        if (campaign.getStartTime() != null && campaign.getStartTime().isBefore(LocalDateTime.now())) {
            return "redirect:/campaigns?error=validation";
        }
        if (campaign.getEndTime() != null && (campaign.getEndTime().isBefore(campaign.getStartTime()) || campaign.getEndTime().isEqual(campaign.getStartTime()))) {
            return "redirect:/campaigns?error=validation";
        }
        
        boolean isDuplicate = campaignService.getAllCampaigns().stream()
            .anyMatch(camp -> camp.getCampaignName().equalsIgnoreCase(campaign.getCampaignName()) 
                           && camp.getVenue().equalsIgnoreCase(campaign.getVenue())
                           && camp.getStartTime().equals(campaign.getStartTime()));
                           
        if (isDuplicate) {
            return "redirect:/campaigns?error=duplicate";
        }
        
        campaignService.createCampaign(campaign, null);
        return "redirect:/campaigns";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long id, @RequestParam String status) {
        campaignService.updateCampaignStatus(id, status);
        return "redirect:/campaigns";
    }

    @PostMapping("/delete")
    public String deleteCampaign(@RequestParam Long id) {
        campaignService.deleteCampaign(id);
        return "redirect:/campaigns";
    }
}


