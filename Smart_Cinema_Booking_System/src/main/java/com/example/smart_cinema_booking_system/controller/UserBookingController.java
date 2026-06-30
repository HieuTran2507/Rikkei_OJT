package com.example.smart_cinema_booking_system.controller;

import com.example.smart_cinema_booking_system.model.dto.HoldBookingRequest;
import com.example.smart_cinema_booking_system.model.dto.HoldBookingResponse;
import com.example.smart_cinema_booking_system.model.entity.Movie;
import com.example.smart_cinema_booking_system.model.entity.Showtime;
import com.example.smart_cinema_booking_system.repository.BookingRepository;
import com.example.smart_cinema_booking_system.repository.ShowtimeRepository;
import com.example.smart_cinema_booking_system.service.BookingService;
import com.example.smart_cinema_booking_system.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserBookingController {

    private final ShowtimeService showtimeService;
    private final BookingService bookingService;

    @GetMapping("/booking")
    public String bookingPage(Model model) {
        List<Showtime> showtimes = showtimeService.findAll();
        model.addAttribute("showtimes", showtimes);
        return "user/booking-content";
    }

    @GetMapping("/booking/seats-status/{showtimeId}")
    @ResponseBody
    public ResponseEntity<?> getSeatsStatus(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(bookingService.getSeatsStatus(showtimeId));
    }

    @PostMapping("/booking/hold")
    @ResponseBody
    public ResponseEntity<?> holdSeats(@RequestBody HoldBookingRequest request) {
        try {
            HoldBookingResponse response = bookingService.holdSeats(request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/booking/pay/{bookingId}")
    @ResponseBody
    public ResponseEntity<?> payBooking(@PathVariable Long bookingId) {
        try {
            bookingService.payBooking(bookingId);
            return ResponseEntity.ok("Thanh toán thành công");

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}
