package com.example.smart_cinema_booking_system.controller;

import com.example.smart_cinema_booking_system.model.entity.Booking;
import com.example.smart_cinema_booking_system.model.entity.User;
import com.example.smart_cinema_booking_system.service.BookingService;
import com.example.smart_cinema_booking_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserHistoryController {
    private final BookingService bookingService;
    private final UserService userService;

    private final int pageSize = 2;

    @GetMapping("/history")
    public String historyPage(
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "0") int page,
        Model model
    ) {
        Pageable pageable = PageRequest.of(page,pageSize);
        User currentUser = userService.getCurrentUser();

        Page<Booking> history = bookingService.getHistory(currentUser.getUserId(), keyword, pageable);
        model.addAttribute("history", history);
        model.addAttribute("keyword", keyword);

        return "user/booking-history";
    }
}
