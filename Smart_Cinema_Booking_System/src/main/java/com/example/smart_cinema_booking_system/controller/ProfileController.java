package com.example.smart_cinema_booking_system.controller;

import com.example.smart_cinema_booking_system.model.dto.UpdateCredentialRequest;
import com.example.smart_cinema_booking_system.model.dto.UpdateProfileRequest;
import com.example.smart_cinema_booking_system.model.entity.User;
import com.example.smart_cinema_booking_system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
    public ResponseEntity<?> updateProfile(
            @Valid
            @RequestBody
            UpdateProfileRequest request
    ) {
        us.updateProfile(request);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/update-credential")
    @ResponseBody
    public ResponseEntity<?> updateCredential(
            @Valid
            @RequestBody
            UpdateCredentialRequest request,
            HttpServletRequest req,
            HttpServletResponse res
    ) {
        us.updateCredential(request);

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        new SecurityContextLogoutHandler()
                .logout(req, res, authentication);

        return ResponseEntity.ok("success");
    }
}
