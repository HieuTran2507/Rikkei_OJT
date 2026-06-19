package com.example.smart_cinema_booking_system.service;

import com.example.smart_cinema_booking_system.exception.FieldException;
import com.example.smart_cinema_booking_system.exception.UserAlreadyExistsException;
import com.example.smart_cinema_booking_system.model.ENUM.UserRole;
import com.example.smart_cinema_booking_system.model.dto.RegisterRequest;
import com.example.smart_cinema_booking_system.model.dto.UpdateCredentialRequest;
import com.example.smart_cinema_booking_system.model.dto.UpdateProfileRequest;
import com.example.smart_cinema_booking_system.model.entity.User;
import com.example.smart_cinema_booking_system.repository.UserRepository;
import com.example.smart_cinema_booking_system.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public User getCurrentUser(){
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        return userDetails.getUser();
    }

    @Transactional
    public void updateProfile(UpdateProfileRequest request) {

        User user = getCurrentUser();

        if (!user.getEmail().equals(request.getEmail()) &&
                ur.existsByEmail(request.getEmail())) {
            throw new RuntimeException(
                    "Email đã tồn tại"
            );
        }

        user.setEmail(request.getEmail());

        user.setFullName(request.getFullName());

        user.setPhone(request.getPhone());

        user.setAddress(request.getAddress());

        user.setUpdatedAt(LocalDateTime.now());

        ur.save(user);
    }

    @Transactional
    public void updateCredential(UpdateCredentialRequest request){
        User user = getCurrentUser();
        if(!user.getUsername().equals(request.getCurrentUsername())){
            throw new FieldException(
                    "currentUsername",
                    "Username không đúng"
            );
        }

        if(!pe.matches(request.getCurrentPassword(), user.getPassword())){
            throw new FieldException(
                    "currentPassword",
                    "Password không đúng"
            );
        }

        if(ur.existsByUsername(request.getNewUsername()) &&
                !request.getNewUsername().equals(user.getUsername())){
            throw new FieldException(
                    "newUsername",
                    "Username đã tồn tại"
            );
        }

        user.setUsername(request.getNewUsername());

        user.setPassword(pe.encode(request.getNewPassword()));

        user.setUpdatedAt(LocalDateTime.now());

        ur.save(user);
    }
}
