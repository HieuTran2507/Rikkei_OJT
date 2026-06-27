package com.example.smart_cinema_booking_system.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class HoldBookingResponse {

    private Long bookingId;

    private LocalDateTime expiredAt;
}
