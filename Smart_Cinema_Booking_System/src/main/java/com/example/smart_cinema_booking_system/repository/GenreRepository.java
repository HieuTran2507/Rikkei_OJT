package com.example.smart_cinema_booking_system.repository;

import com.example.smart_cinema_booking_system.model.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}