package com.bloodbank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.repository.BloodStockRepository;
import com.bloodbank.repository.BloodRequestRepository;

@Controller
public class DashboardController {

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private BloodStockRepository inventoryRepository;

    @Autowired
    private BloodRequestRepository requestRepository;

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("title", "Dashboard");
        model.addAttribute("totalDonors", donorRepository.count());
        model.addAttribute("totalInventory", inventoryRepository.count());
        model.addAttribute("totalRequests", requestRepository.count());
        return "dashboard";
    }

    
}