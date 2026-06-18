package com.example.smart_cinema_booking_system.controller;

import com.example.smart_cinema_booking_system.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addUserInfo(Model model) {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth != null
                && auth.isAuthenticated()
                && auth.getPrincipal()
                instanceof CustomUserDetails userDetails) {

            model.addAttribute(
                    "fullName",
                    userDetails.getFullName()
            );
        }
    }
}