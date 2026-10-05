package com.bloodbank.controller;

import com.bloodbank.model.Distribution;
import com.bloodbank.service.LogisticsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/logistics")
public class LogisticsController {

    @Autowired
    private LogisticsService logisticsService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("title", "Distribution & Logistics");
        model.addAttribute("distributions", logisticsService.getAllDistributions());
        return "logistics";
    }

    @PostMapping("/dispatch")
    public String dispatch(@Valid @ModelAttribute Distribution distribution, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/logistics?error=validation";
        }
        
        boolean isDuplicate = logisticsService.getAllDistributions().stream()
            .anyMatch(dist -> dist.getDriverName().equalsIgnoreCase(distribution.getDriverName()) 
                           && dist.getVehicleRegistration().equalsIgnoreCase(distribution.getVehicleRegistration())
                           && dist.getRouteDetails().equalsIgnoreCase(distribution.getRouteDetails())
                           && "IN_TRANSIT".equals(dist.getStatus()));
        
        if (isDuplicate) {
            return "redirect:/logistics?error=duplicate";
        }
        
        distribution.setCurrentTemperature(4.0); // optimal starting temp
        logisticsService.createDispatch(distribution);
        return "redirect:/logistics";
    }

    @PostMapping("/update")
    public String updateLogistics(@RequestParam Long id,
                                  @RequestParam(required = false) String status,
                                  @RequestParam(required = false) Double temp,
                                  @RequestParam(required = false) String gps) {
        logisticsService.updateStatus(id, status, temp, gps);
        return "redirect:/logistics";
    }

    @PostMapping("/delete")
    public String deleteLogistics(@RequestParam Long id) {
        logisticsService.deleteDistribution(id);
        return "redirect:/logistics";
    }
}
