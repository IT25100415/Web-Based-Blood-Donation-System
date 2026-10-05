package com.bloodbank.controller;

import com.bloodbank.model.BloodRequest;
import com.bloodbank.model.Hospital;
import com.bloodbank.model.User;
import com.bloodbank.repository.UserRepository;
import com.bloodbank.repository.HospitalRepository;
import com.bloodbank.service.HospitalRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private HospitalRequestService requestService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HospitalRepository hospitalRepository;

    @GetMapping
    public String index(Model model, Principal principal) {
        model.addAttribute("title", "Hospital Requests");
        
        User user = userRepository.findByEmail(principal.getName());
        if (user.getRole() == User.Role.HOSPITAL) {
            model.addAttribute("requests", requestService.getRequestsByHospitalId(user.getId()));
        } else {
            model.addAttribute("requests", requestService.getAllRequests());
        }
        
        return "requests";
    }

    @PostMapping("/submit")
    public String submitRequest(@Valid @ModelAttribute BloodRequest request, BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "redirect:/requests?error=validation";
        }
        if (request.getNeededDate() != null && request.getNeededDate().isBefore(LocalDateTime.now())) {
            return "redirect:/requests?error=invalid_date";
        }
        
        User user = userRepository.findByEmail(principal.getName());
        if (user.getRole() == User.Role.HOSPITAL) {
            Hospital hospital = hospitalRepository.findById(user.getId()).orElse(null);
            request.setHospital(hospital);
        }
        
        requestService.submitRequest(request);
        return "redirect:/requests";
    }

    @PostMapping("/update")
    public String updateRequest(@RequestParam Long id, @Valid @ModelAttribute BloodRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/requests?error=validation";
        }
        if (request.getNeededDate() != null && request.getNeededDate().isBefore(LocalDateTime.now())) {
            return "redirect:/requests?error=invalid_date";
        }
        
        requestService.updateRequest(id, request);
        return "redirect:/requests";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long id, @RequestParam String status, Principal principal) {
        requestService.updateRequestStatus(id, BloodRequest.Status.valueOf(status));
        return "redirect:/requests";
    }

    @PostMapping("/delete")
    public String deleteRequest(@RequestParam Long id) {
        requestService.deleteRequest(id);
        return "redirect:/requests";
    }
}
