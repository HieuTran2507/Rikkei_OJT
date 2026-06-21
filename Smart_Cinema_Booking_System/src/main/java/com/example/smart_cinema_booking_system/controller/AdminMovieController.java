package com.example.smart_cinema_booking_system.controller;

import com.example.smart_cinema_booking_system.model.ENUM.MovieStatus;
import com.example.smart_cinema_booking_system.model.dto.MovieRequest;
import com.example.smart_cinema_booking_system.model.entity.Movie;
import com.example.smart_cinema_booking_system.repository.GenreRepository;
import com.example.smart_cinema_booking_system.service.MovieService;
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
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class AdminMovieController {

    private final MovieService movieService;
    private final GenreRepository genreRepo;
    private final int pageSize = 2;

    // LOAD PAGE
    @GetMapping("/content")
    public String content(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Movie> movies = movieService.getMovies(keyword, pageable);

        model.addAttribute("movies", movies);
        model.addAttribute("keyword", keyword);

        return "admin/movie-content";
    }

    // FORM DATA (modal init)
    @ModelAttribute
    public void init(Model model) {
        model.addAttribute("genres", genreRepo.findAll());
        model.addAttribute("statuses", MovieStatus.values());
    }

    // CREATE
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createMovie(
            @Valid @ModelAttribute MovieRequest req,
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

        movieService.save(req);

        return ResponseEntity.ok("success");
    }

    // UPDATE
    @PostMapping("/update")
    public ResponseEntity<?> update(
            @Valid @ModelAttribute MovieRequest req,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(e ->
                    errors.put(e.getField(), e.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }
        movieService.save(req);
        return ResponseEntity.ok("success");
    }

    // DELETE
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.ok("deleted");
    }
}
