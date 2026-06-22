package com.example.smart_cinema_booking_system.service;

import com.example.smart_cinema_booking_system.model.dto.MovieRequest;
import com.example.smart_cinema_booking_system.model.entity.Genre;
import com.example.smart_cinema_booking_system.model.entity.Movie;
import com.example.smart_cinema_booking_system.repository.BookingRepository;
import com.example.smart_cinema_booking_system.repository.GenreRepository;
import com.example.smart_cinema_booking_system.repository.MovieRepository;
import com.example.smart_cinema_booking_system.repository.ShowtimeRepository;
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
    private final ShowtimeRepository showtimeRepo;
    private final BookingRepository bookingRepo;

    private final String UPLOAD_DIR = "uploads/posters/";

    // show movies, pagination, search
    public Page<Movie> getMovies(String keyword, Pageable pageable) {

        if (keyword == null || keyword.isBlank()) {
            return movieRepo.findAll(pageable);
        }

        return movieRepo.findByTitleContainingIgnoreCase(keyword, pageable);
    }

    // create movie
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
        if(req.getPoster() != null && !req.getPoster().isEmpty()){

            fileUploadService.deletePoster(movie.getPosterUrl());

            movie.setPosterUrl(fileUploadService.uploadPoster(req.getPoster()));
        }
        movieRepo.save(movie);
    }

    // delete movie
    public void delete(Long movieId) {

        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));

        // 1. Có suất chiếu
        if (showtimeRepo.existsByMovieMovieId(movieId)) {
            throw new RuntimeException(
                    "Không thể xóa phim đang có suất chiếu"
            );
        }

        // 2. Có người đặt vé
        if (bookingRepo.countByShowtimeMovieMovieId(movieId) > 0) {
            throw new RuntimeException(
                    "Không thể xóa phim đã có người đặt vé"
            );
        }

        movieRepo.delete(movie);
        fileUploadService.deletePoster(movie.getPosterUrl());
    }

    //
    public Movie findById(Long id){
        return movieRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("không tìm thấy phim"));
    }


}