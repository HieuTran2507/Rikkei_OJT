package com.example.smart_cinema_booking_system.service;

import com.example.smart_cinema_booking_system.model.ENUM.BookingStatus;
import com.example.smart_cinema_booking_system.model.ENUM.TicketStatus;
import com.example.smart_cinema_booking_system.model.dto.HoldBookingRequest;
import com.example.smart_cinema_booking_system.model.dto.HoldBookingResponse;
import com.example.smart_cinema_booking_system.model.entity.Booking;
import com.example.smart_cinema_booking_system.model.entity.Showtime;
import com.example.smart_cinema_booking_system.model.entity.Ticket;
import com.example.smart_cinema_booking_system.model.entity.User;
import com.example.smart_cinema_booking_system.repository.BookingRepository;
import com.example.smart_cinema_booking_system.repository.ShowtimeRepository;
import com.example.smart_cinema_booking_system.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final int HOLD_SECONDS = 30;

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final ShowtimeRepository showtimeRepository;
    private final UserService userService;

    // trạng thái ghế (PENDING, PAID)
    public List<Map<String, String>> getSeatsStatus(Long showtimeId) {
        clearExpiredPendingBookings();
        LocalDateTime validFrom = LocalDateTime.now().minusSeconds(HOLD_SECONDS);
        List<Ticket> locked = ticketRepository.findLockedTickets(showtimeId, validFrom);
        return locked.stream()
                .map(t -> Map.of("seatCode", t.getSeatCode(), "status", t.getStatus().name()))
                .toList();
    }

    // giữ ghế trong vòng n(s) để thanh toán
    @Transactional
    public HoldBookingResponse holdSeats(HoldBookingRequest request) {
        clearExpiredPendingBookings();

        if (request.getShowtimeId() == null) {
            throw new RuntimeException("Vui lòng chọn suất chiếu");
        }

        if (request.getSeatCodes() == null || request.getSeatCodes().isEmpty()) {
            throw new RuntimeException("Vui lòng chọn ghế");
        }

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

        User user = userService.getCurrentUser();

        LocalDateTime validFrom = LocalDateTime.now().minusSeconds(HOLD_SECONDS);

        boolean hasLockedSeat = ticketRepository.existsLockedSeats(
                request.getShowtimeId(),
                request.getSeatCodes(),
                validFrom
        );

        if (hasLockedSeat) {
            throw new RuntimeException("Một hoặc nhiều ghế đã được giữ hoặc đã thanh toán. Vui lòng load lại trang.");
        }

        BigDecimal totalAmount = showtime.getTicketPrice()
                .multiply(BigDecimal.valueOf(request.getSeatCodes().size()));

        Booking booking = Booking.builder()
                .bookingDate(LocalDateTime.now())
                .totalAmount(totalAmount)
                .paymentMethod(request.getPaymentMethod())
                .bookingStatus(BookingStatus.PENDING)
                .bookingSeat(String.join(",", request.getSeatCodes()))
                .user(user)
                .showtime(showtime)
                .build();

        try {
            Booking savedBooking = bookingRepository.save(booking);

            List<Ticket> tickets = request.getSeatCodes()
                    .stream()
                    .map(seatCode -> Ticket.builder()
                            .seatCode(seatCode)
                            .price(showtime.getTicketPrice())
                            .status(TicketStatus.PENDING)
                            .booking(savedBooking)
                            .showtime(showtime)
                            .build())
                    .toList();

            ticketRepository.saveAll(tickets);

            return new HoldBookingResponse(
                    savedBooking.getBookingId(),
                    savedBooking.getBookingDate().plusSeconds(HOLD_SECONDS)
            );

        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Ghế vừa được người khác giữ, vui lòng chọn ghế khác");
        }
    }

    // thanh toán trong vong n(s)
    @Transactional
    public void payBooking(Long bookingId) {
        clearExpiredPendingBookings();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

        LocalDateTime expiredBefore = LocalDateTime.now().minusSeconds(HOLD_SECONDS);

        if (booking.getBookingDate().isBefore(expiredBefore)) {
            cancelExpiredBooking(booking);
            throw new RuntimeException("Đã hết thời gian giữ ghế");
        }

        List<Ticket> tickets = ticketRepository.findByBookingBookingId(bookingId);

        booking.setBookingStatus(BookingStatus.PAID);

        tickets.forEach(ticket -> ticket.setStatus(TicketStatus.PAID));

        ticketRepository.saveAll(tickets);
        bookingRepository.save(booking);
    }

    @Transactional
    public void clearExpiredPendingBookings() {
        LocalDateTime expiredBefore = LocalDateTime.now().minusSeconds(HOLD_SECONDS);

        List<Booking> expiredBookings =
                bookingRepository.findExpiredPendingBookings(
                        BookingStatus.PENDING,
                        expiredBefore
                );

        expiredBookings.forEach(this::cancelExpiredBooking);
    }

    private void cancelExpiredBooking(Booking booking) {
        booking.setBookingStatus(BookingStatus.CANCELLED);

        List<Ticket> tickets =
                ticketRepository.findByBookingBookingId(booking.getBookingId());

        ticketRepository.deleteAll(tickets);

        bookingRepository.save(booking);
    }

    public Page<Booking> getHistory(Long userId, String keyword, Pageable pageable){
        if(keyword == null || keyword.isBlank()){
            return bookingRepository.findAllHistory(userId,pageable);
        }

        return bookingRepository.SearchHistory(userId,keyword,pageable);
    }
}
