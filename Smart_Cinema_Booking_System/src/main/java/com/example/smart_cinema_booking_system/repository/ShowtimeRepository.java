package com.example.smart_cinema_booking_system.repository;

import com.example.smart_cinema_booking_system.model.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    boolean existsByMovieMovieIdAndStartTimeAfter(
            Long movieId,
            LocalDateTime now
    );
}
