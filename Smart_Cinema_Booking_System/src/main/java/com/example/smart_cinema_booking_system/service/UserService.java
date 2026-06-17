package com.example.smart_cinema_booking_system.service;

import com.example.smart_cinema_booking_system.exception.UserAlreadyExistsException;
import com.example.smart_cinema_booking_system.model.ENUM.UserRole;
import com.example.smart_cinema_booking_system.model.dto.RegisterRequest;
import com.example.smart_cinema_booking_system.model.entity.User;
import com.example.smart_cinema_booking_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserRepository ur;

    @Autowired
    private PasswordEncoder pe;

    public void register(RegisterRequest request) {

        if (ur.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException(
                    "Username đã tồn tại");
        }

        if (ur.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email đã tồn tại");
        }

        User user = new User();

        user.setUsername(request.getUsername());

        user.setPassword(pe.encode(
                request.getPassword()
                )
        );

        user.setEmail(request.getEmail());

        user.setFullName(request.getFullName());

        user.setPhone(request.getPhone());

        user.setAddress(request.getAddress());

        user.setRole(UserRole.USER);

        user.setEnabled(true);

        user.setCreatedAt(LocalDateTime.now());

        user.setUpdatedAt(LocalDateTime.now());

        ur.save(user);
    }
}
