package com.example.smart_cinema_booking_system.controller;

import com.example.smart_cinema_booking_system.model.dto.UpdateProfileRequest;
import com.example.smart_cinema_booking_system.model.entity.User;
import com.example.smart_cinema_booking_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserService us;

    @GetMapping("/content")
    public String profileContent(Model model) {
        User user = us.getCurrentUser();

        model.addAttribute("user", user);
        return "profile/profile-content";
    }

    @PostMapping("/update")
    @ResponseBody
    public String updateProfile(@RequestBody UpdateProfileRequest request) {
        us.updateProfile(request);
        return "success";
    }
}
