package com.example.smart_cinema_booking_system.repository;

import com.example.smart_cinema_booking_system.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByBookingBookingId(Long bookingId);

    @Query("""
        SELECT COUNT(t) > 0
        FROM Ticket t
        WHERE t.showtime.showtimeId = :showtimeId
        AND t.seatCode IN :seatCodes
        AND (
            t.status = com.example.smart_cinema_booking_system.model.ENUM.TicketStatus.PAID
            OR (
                t.status = com.example.smart_cinema_booking_system.model.ENUM.TicketStatus.PENDING
                AND t.booking.bookingDate > :validFrom
            )
        )
    """)
    boolean existsLockedSeats(
            Long showtimeId,
            List<String> seatCodes,
            LocalDateTime validFrom
    );

    @Query("""
        SELECT t FROM Ticket t
        WHERE t.showtime.showtimeId = :showtimeId
        AND (
            t.status = com.example.smart_cinema_booking_system.model.ENUM.TicketStatus.PAID
            OR (
                t.status = com.example.smart_cinema_booking_system.model.ENUM.TicketStatus.PENDING
                AND t.booking.bookingDate > :validFrom
            )
        )
    """)
    List<Ticket> findLockedTickets(Long showtimeId, LocalDateTime validFrom);

    List<Ticket> findByBookingBookingStatusAndBookingBookingDateBefore(
            com.example.smart_cinema_booking_system.model.ENUM.BookingStatus status,
            LocalDateTime expiredBefore
    );
}
