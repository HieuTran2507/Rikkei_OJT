package com.example.smart_cinema_booking_system.repository;

import com.example.smart_cinema_booking_system.model.ENUM.BookingStatus;
import com.example.smart_cinema_booking_system.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    long countByShowtimeMovieMovieId(Long movieId);

    boolean existsByShowtimeShowtimeId(Long showtimeId);

    @Query("""
    SELECT b
    FROM Booking b
    WHERE b.bookingStatus = :status
    AND b.bookingDate < :expiredBefore
""")
    List<Booking> findExpiredPendingBookings(
            BookingStatus status,
            LocalDateTime expiredBefore
    );
}