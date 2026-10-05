package com.bloodbank.controller;

import com.bloodbank.model.Donor;
import com.bloodbank.model.HealthScreening;
import com.bloodbank.model.User;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.repository.UserRepository;
import com.bloodbank.service.HealthScreeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/health-screening")
public class HealthScreeningController {

    @Autowired
    private HealthScreeningService service;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DonorRepository donorRepository;

    @GetMapping
    public String index(Model model, Principal principal) {
        model.addAttribute("title", "Health Screening");
        
        User user = userRepository.findByEmail(principal.getName());
        if (user.getRole() == User.Role.DONOR) {
            model.addAttribute("screenings", service.getScreeningsByDonorId(user.getId()));
        } else {
            model.addAttribute("screenings", service.getAllScreenings());
        }
        
        return "health-screening";
    }

    @PostMapping("/request")
    public String requestScreening(@RequestParam(required = false) String[] tests, Principal principal) {
        HealthScreening screening = new HealthScreening();
        if (tests != null && tests.length > 0) {
            screening.setRequestedTests(String.join(", ", tests));
        } else {
            screening.setRequestedTests("Standard Panel");
        }
        
        User user = userRepository.findByEmail(principal.getName());
        if (user.getRole() == User.Role.DONOR) {
            Donor donor = donorRepository.findById(user.getId()).orElse(null);
            screening.setDonor(donor);
        }
        
        service.requestScreening(screening);
        return "redirect:/health-screening";
    }
    
    @PostMapping("/update-request")
    public String updateScreeningRequest(@RequestParam Long id, @RequestParam(required = false) String[] tests, Principal principal) {
        HealthScreening screening = service.getScreeningById(id);
        if (screening != null && screening.getStatus() == HealthScreening.ScreeningStatus.PENDING) {
            
            // Security check: Only the owner or an admin/staff can update
            User user = userRepository.findByEmail(principal.getName());
            if (user.getRole() == User.Role.DONOR) {
                if (screening.getDonor() == null || !screening.getDonor().getId().equals(user.getId())) {
                    return "redirect:/health-screening?error=unauthorized";
                }
            }
            
            if (tests != null && tests.length > 0) {
                screening.setRequestedTests(String.join(", ", tests));
            } else {
                screening.setRequestedTests("Standard Panel");
            }
            // we can just save it directly since we fetched it, but best to go through service
            // I'll update the tests and re-save
            service.updateScreeningTests(id, screening.getRequestedTests());
        }
        return "redirect:/health-screening";
    }

    @PostMapping("/approve")
    public String approveScreening(@RequestParam Long id, @RequestParam String scheduledDateStr) {
        LocalDateTime scheduledDate = LocalDateTime.parse(scheduledDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        if (scheduledDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Error: Health screening cannot be scheduled in the past.");
        }
        service.approveAndSchedule(id, scheduledDate);
        return "redirect:/health-screening";
    }

    @PostMapping("/results")
    public String submitResults(@RequestParam Long id, 
                                @RequestParam String hepB, 
                                @RequestParam String hepC, 
                                @RequestParam String hiv, 
                                @RequestParam String dengue, 
                                @RequestParam String bloodGroup) {
        service.updateResults(id, hepB, hepC, hiv, dengue, bloodGroup);
        return "redirect:/health-screening";
    }
    
    @PostMapping("/cancel")
    public String cancelScreening(@RequestParam Long id) {
        service.cancelScreening(id);
        return "redirect:/health-screening";
    }
    
    @PostMapping("/delete")
    public String deleteScreening(@RequestParam Long id) {
        service.deleteScreening(id);
        return "redirect:/health-screening";
    }
}

