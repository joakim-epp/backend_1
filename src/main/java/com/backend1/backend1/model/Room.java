package com.backend1.backend1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomNumber;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    private int extraBeds;

    private BigDecimal pricePerNight;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();
}
