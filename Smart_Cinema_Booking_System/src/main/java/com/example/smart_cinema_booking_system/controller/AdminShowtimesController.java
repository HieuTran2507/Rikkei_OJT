package com.example.smart_cinema_booking_system.controller;

import com.example.smart_cinema_booking_system.model.dto.MovieRequest;
import com.example.smart_cinema_booking_system.model.dto.ShowtimeRequest;
import com.example.smart_cinema_booking_system.model.dto.ShowtimeResponse;
import com.example.smart_cinema_booking_system.model.entity.Showtime;
import com.example.smart_cinema_booking_system.service.MovieService;
import com.example.smart_cinema_booking_system.service.RoomService;
import com.example.smart_cinema_booking_system.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/showtimes")
@RequiredArgsConstructor
public class AdminShowtimesController {
    private final ShowtimeService showtimeService;
    private final MovieService movieService;
    private final RoomService roomService;
    private final int pageSize = 2;

    // LOAD PAGE
    @GetMapping("/content")
    public String content(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page,pageSize);

        Page<Showtime> showtimes = showtimeService.getShowtimes(keyword,pageable);

        model.addAttribute("showtimes", showtimes);
        model.addAttribute("keyword", keyword);

        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("rooms", roomService.getAllRooms());

        return "admin/showtime-content";
    }

    // CREATE
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createShowtime(
            @Valid @ModelAttribute ShowtimeRequest req,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            Map<String,String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errors.put(
                        error.getField(),
                        error.getDefaultMessage()
                );
            });

            return ResponseEntity.badRequest().body(errors);
        }

        showtimeService.save(req);

        return ResponseEntity.ok("success");
    }

    // DELETE
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            showtimeService.delete(id);
            return ResponseEntity.ok("deleted");

        } catch (RuntimeException ex) {

            return ResponseEntity
                    .badRequest()
                    .body(ex.getMessage());
        }
    }

    // EDIT
    @GetMapping("/{id}")
    @ResponseBody
    public ShowtimeResponse getShowtime(@PathVariable Long id){
        return showtimeService.getResponseById(id);
    }

    // UPDATE
    @PostMapping("/update")
    public ResponseEntity<?> update(
            @Valid @ModelAttribute ShowtimeRequest req,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(e ->
                    errors.put(e.getField(), e.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }
        showtimeService.save(req);
        return ResponseEntity.ok("success");
    }
}
