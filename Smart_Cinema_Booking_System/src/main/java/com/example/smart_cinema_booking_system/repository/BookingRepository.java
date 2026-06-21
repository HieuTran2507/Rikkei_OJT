package com.example.smart_cinema_booking_system.repository;

import com.example.smart_cinema_booking_system.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.showtime.movie.movieId = :movieId
    """)
    long countByMovieId(Long movieId);
}