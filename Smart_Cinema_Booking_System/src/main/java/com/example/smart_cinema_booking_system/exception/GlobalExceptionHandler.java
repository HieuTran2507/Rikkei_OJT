package com.example.smart_cinema_booking_system.exception;

import com.example.smart_cinema_booking_system.model.dto.RegisterRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserAlreadyExists(
            UserAlreadyExistsException ex,
            Model model
    ) {

        model.addAttribute(
                "errorMessage",
                ex.getMessage()
        );

        model.addAttribute(
                "registerRequest",
                new RegisterRequest()
        );

        return "auth/register";
    }
}
