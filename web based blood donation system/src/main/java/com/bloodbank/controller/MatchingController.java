package com.bloodbank.controller;

import com.bloodbank.model.Donor;
import com.bloodbank.model.User;
import com.bloodbank.repository.UserRepository;
import com.bloodbank.service.MatchingService;
import com.bloodbank.service.HospitalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/matching")
public class MatchingController {

    @Autowired
    private MatchingService matchingService;
    
    @Autowired
    private HospitalRequestService requestService;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String index(Model model, 
                        @RequestParam(required = false) Long requestId,
                        @RequestParam(required = false) String bloodGroup, 
                        @RequestParam(required = false) String city,
                        Principal principal) {
        model.addAttribute("title", "Smart Matching");
        
        User user = userRepository.findByEmail(principal.getName());
        if (user.getRole() == User.Role.HOSPITAL) {
            model.addAttribute("history", matchingService.getMatchesByHospitalId(user.getId()));
            model.addAttribute("requests", requestService.getRequestsByHospitalId(user.getId()));
        } else {
            model.addAttribute("history", matchingService.getAllMatches());
            model.addAttribute("requests", requestService.getAllRequests());
        }
        
        if (bloodGroup != null && !bloodGroup.isEmpty()) {
            List<Donor> results = matchingService.findMatchingDonors(bloodGroup, city);
            model.addAttribute("searchResults", results);
            model.addAttribute("searchReqId", requestId);
            model.addAttribute("searchBg", bloodGroup);
            model.addAttribute("searchCity", city);
        }
        
        return "matching";
    }

    @PostMapping("/notify")
    public String notifyDonor(@RequestParam Long requestId, @RequestParam Long donorId) {
        matchingService.notifyDonor(requestId, donorId);
        return "redirect:/matching";
    }
    
    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long id, @RequestParam String status) {
        System.out.println("====== UPDATE STATUS CALLED ======");
        System.out.println("ID: " + id + ", STATUS: " + status);
        matchingService.updateStatus(id, status);
        System.out.println("====== UPDATE FINISHED ======");
        return "redirect:/matching";
    }
    
    @PostMapping("/delete")
    public String deleteMatch(@RequestParam Long id) {
        matchingService.deleteMatch(id);
        return "redirect:/matching";
    }
}
