package com.example.smart_cinema_booking_system.model.dto;

import com.example.smart_cinema_booking_system.model.ENUM.ShowtimeStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ShowtimeResponse {

    private Long showtimeId;

    private Long movieId;

    private Long roomId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BigDecimal ticketPrice;

    private ShowtimeStatus status;
}
