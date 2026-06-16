package com.example.smart_cinema_booking_system.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "room_name", length = 50)
    private String roomName;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @Column(name = "seats_x")
    private Integer seatsX;

    @Column(name = "seats_y")
    private Integer seatsY;

    @Column(name = "vip_seats", columnDefinition = "TEXT")
    private String vipSeats;

    @Column(name = "couple_seats", columnDefinition = "TEXT")
    private String coupleSeats;

    private Boolean status;

    @OneToMany(mappedBy = "room")
    private List<Showtime> showtimes;
}
