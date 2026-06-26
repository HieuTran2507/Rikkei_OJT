package com.example.smart_cinema_booking_system.repository;

import com.example.smart_cinema_booking_system.model.entity.Movie;
import com.example.smart_cinema_booking_system.model.entity.Showtime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    boolean existsByMovieMovieId(Long movieId);
    @Query("""
            SELECT s
            FROM Showtime s
            WHERE LOWER(s.movie.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(s.room.roomName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Showtime> search(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
            SELECT COUNT(s)
            FROM Showtime s
            WHERE s.room.roomId = :roomId
            AND (:startTime < s.endTime AND :endTime > s.startTime)
    """)
    long countConflict(
            Long roomId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}
