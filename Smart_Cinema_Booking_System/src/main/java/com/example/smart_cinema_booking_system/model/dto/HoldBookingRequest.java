package com.example.smart_cinema_booking_system.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HoldBookingRequest {

    private Long showtimeId;

    private List<String> seatCodes;

    private String paymentMethod;
}
