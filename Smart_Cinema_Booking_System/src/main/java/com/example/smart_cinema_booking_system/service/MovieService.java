package com.example.smart_cinema_booking_system.service;

import com.example.smart_cinema_booking_system.model.dto.MovieRequest;
import com.example.smart_cinema_booking_system.model.entity.Genre;
import com.example.smart_cinema_booking_system.model.entity.Movie;
import com.example.smart_cinema_booking_system.repository.GenreRepository;
import com.example.smart_cinema_booking_system.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepo;
    private final GenreRepository genreRepo;
    private final FileUploadService fileUploadService;

    private final String UPLOAD_DIR = "uploads/posters/";

    // show movies, pagination, search
    public Page<Movie> getMovies(String keyword, Pageable pageable) {

        if (keyword == null || keyword.isBlank()) {
            return movieRepo.findAll(pageable);
        }

        return movieRepo.findByTitleContainingIgnoreCase(keyword, pageable);
    }

    // create, update movie
    public void save(MovieRequest req) {

        Movie movie = (req.getMovieId() != null)
                ? movieRepo.findById(req.getMovieId()).orElse(new Movie())
                : new Movie();

        movie.setTitle(req.getTitle());
        movie.setDescription(req.getDescription());
        movie.setDuration(req.getDuration());
        movie.setReleaseDate(req.getReleaseDate());
        movie.setLanguage(req.getLanguage());
        movie.setStatus(req.getStatus());

        // GENRES
        List<Genre> genres = genreRepo.findAllById(req.getGenreIds());
        movie.setGenres(genres);

        // POSTER
        String posterUrl = fileUploadService.uploadPoster(req.getPoster());
        movie.setPosterUrl(posterUrl);

        movieRepo.save(movie);
    }

    // delete movie
    public void delete(Long id) {

        Movie movie = movieRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        boolean hasBooking =
                movie.getShowtimes()
                        .stream()
                        .anyMatch(s -> !s.getBookings().isEmpty());

        if (hasBooking) {
            throw new RuntimeException("Movie has bookings or showtimes");
        }

        movieRepo.delete(movie);
    }
}