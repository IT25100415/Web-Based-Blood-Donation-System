package com.bloodbank.controller;

import com.bloodbank.model.User;
import com.bloodbank.repository.UserRepository;
import com.bloodbank.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/donor/alerts")
public class MatchAlertsController {

    @Autowired
    private MatchingService matchingService;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String index(Model model, Principal principal) {
        model.addAttribute("title", "Match Alerts");
        User user = userRepository.findByEmail(principal.getName());
        model.addAttribute("alerts", matchingService.getMatchesByDonorId(user.getId()));
        return "donor-alerts";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long id, @RequestParam String status) {
        matchingService.updateStatus(id, status);
        return "redirect:/donor/alerts";
    }
}
