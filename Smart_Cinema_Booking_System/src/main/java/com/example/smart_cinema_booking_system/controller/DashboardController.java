package com.example.smart_cinema_booking_system.controller;

import com.example.smart_cinema_booking_system.model.entity.User;
import com.example.smart_cinema_booking_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    @Autowired
    private UserService us;

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard/home";
    }

    @GetMapping("/dashboard/header")
    public String headerFragment(Model model) {
        User user = us.getCurrentUser();
        model.addAttribute("fullName", user.getFullName());
        return "fragments/header :: header";
    }
}
