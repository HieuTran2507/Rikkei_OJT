package com.example.smart_cinema_booking_system.service;

import com.example.smart_cinema_booking_system.model.entity.Movie;
import com.example.smart_cinema_booking_system.model.entity.Room;
import com.example.smart_cinema_booking_system.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepo;

    // get all movie
    public List<Room> getAllRooms(){
        return roomRepo.findAll();
    }
}
