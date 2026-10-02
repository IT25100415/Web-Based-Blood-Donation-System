package com.bloodbank.controller;

import com.bloodbank.model.User;
import com.bloodbank.model.Notification;
import com.bloodbank.repository.UserRepository;
import com.bloodbank.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/console")
public class ConsoleController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("title", "System Console");
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("recentLogs", notificationRepository.findAll().stream().sorted((a,b)->b.getSentTime().compareTo(a.getSentTime())).limit(50).toList());
        return "console";
    }

    @PostMapping("/broadcast")
    public String broadcast(@RequestParam String message) {
        for (User user : userRepository.findAll()) {
            Notification n = new Notification();
            n.setUser(user);
            n.setMessage("[BROADCAST] " + message);
            n.setSentTime(LocalDateTime.now());
            n.setIsRead(false);
            notificationRepository.save(n);
        }
        return "redirect:/console?success=Broadcast+sent+successfully";
    }

    @PostMapping("/role")
    public String changeRole(@RequestParam Long userId, @RequestParam String newRole) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setRole(User.Role.valueOf(newRole));
            userRepository.save(user);
        }
        return "redirect:/console?success=Role+updated+successfully";
    }
}
