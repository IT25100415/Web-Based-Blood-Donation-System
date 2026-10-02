package com.bloodbank.controller;

import com.bloodbank.model.Donor;
import com.bloodbank.model.User;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DonorRepository donorRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @PostMapping("/register")
    public String processRegister(@RequestParam String name,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String nic,
                                  @RequestParam String bloodGroup,
                                  @RequestParam String city,
                                  @RequestParam String phone) {
                                      
        if (userRepository.findByEmail(email) != null) {
            return "redirect:/register?error=email_exists";
        }
        
        try {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(User.Role.DONOR);
            user = userRepository.save(user);
            
            Donor donor = new Donor();
            donor.setUser(user);
            donor.setNic(nic);
            donor.setBloodGroup(bloodGroup);
            donor.setCity(city);
            donor.setPhone(phone);
            donorRepository.save(donor);
            
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            return "redirect:/register?error=invalid_data";
        }
    }
}
