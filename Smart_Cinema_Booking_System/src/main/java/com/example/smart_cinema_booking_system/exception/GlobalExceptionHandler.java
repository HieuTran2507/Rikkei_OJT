package com.example.smart_cinema_booking_system.exception;

import com.example.smart_cinema_booking_system.model.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserAlreadyExists(UserAlreadyExistsException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

        Map<String,String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    errors.put(
                            error.getField(),
                            error.getDefaultMessage()
                    );
                });

        return ResponseEntity
                .badRequest()
                .body(errors);
    }

    @ExceptionHandler(FieldException.class)
    public ResponseEntity<?> handleFieldException(FieldException ex){

        Map<String,String> errors = new HashMap<>();

        errors.put(
                ex.getField(),
                ex.getMessage()
        );

        return ResponseEntity
                .badRequest()
                .body(errors);
    }
}
