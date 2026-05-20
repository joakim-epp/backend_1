package com.backend1.backend1.dto;

import com.backend1.backend1.model.RoomType;

import java.math.BigDecimal;

public record RoomDTO(
        Long id,
        String roomNumber,
        RoomType type,
        int extraBeds,
        BigDecimal pricePerNight,
        int capacity,
        String typeDescription
) {
}
