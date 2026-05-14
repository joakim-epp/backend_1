package com.backend1.backend1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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

    private int extraBeds = 0;
    private BigDecimal pricePerNight;

    public int getCapacity() {
        if (type == RoomType.SINGLE) return 1;
        return 2 + extraBeds;
    }

    public String getTypeDescription() {
        if (type == RoomType.SINGLE) return "Enkelrum (1 person)";
        String extra = extraBeds == 0 ? "inga extrasängar"
                     : extraBeds == 1 ? "1 extrasäng" : "2 extrasängar";
        return "Dubbelrum, " + extra + " (max " + getCapacity() + " pers.)";
    }
}
