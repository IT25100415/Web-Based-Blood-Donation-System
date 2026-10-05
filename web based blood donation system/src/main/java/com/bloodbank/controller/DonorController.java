package com.bloodbank.controller;
import com.bloodbank.model.Donor;
import com.bloodbank.repository.DonorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/donors")
public class DonorController {
    @Autowired private DonorRepository donorRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("title", "Donor Directory");
        model.addAttribute("donors", donorRepository.findAll());
        return "donors";
    }

    @Autowired private com.bloodbank.repository.UserRepository userRepository;

    @PostMapping("/add")
    public String addDonor(@Valid @ModelAttribute Donor donor, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/donors?error=validation";
        }
        try {
            com.bloodbank.model.User u = new com.bloodbank.model.User();
            u.setEmail(donor.getNic() + "@donor.local");
            u.setName("Donor " + donor.getNic());
            u.setPassword("password");
            u.setRole(com.bloodbank.model.User.Role.DONOR);
            userRepository.save(u);
            
            donor.setUser(u);
            donorRepository.save(donor);
            return "redirect:/donors";
        } catch (Exception e) {
            return "redirect:/donors?error=duplicate";
        }
    }

    @PostMapping("/update")
    public String updateDonor(@RequestParam Long id, @RequestParam String bloodGroup, @RequestParam String city, @RequestParam String phone) {
        Donor d = donorRepository.findById(id).orElse(null);
        if (d != null) {
            d.setBloodGroup(bloodGroup); d.setCity(city); d.setPhone(phone);
            donorRepository.save(d);
        }
        return "redirect:/donors";
    }

    @PostMapping("/delete")
    public String deleteDonor(@RequestParam Long id) {
        donorRepository.deleteById(id);
        return "redirect:/donors";
    }
}
