package com.example.smart_cinema_booking_system.exception;

import lombok.Getter;

@Getter
public class FieldException extends RuntimeException {

    private final String field;

    public FieldException(String field, String message) {
        super(message);
        this.field = field;
    }
}
