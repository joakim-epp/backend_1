package com.backend1.backend1.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingDTO {
    private Long id;
    private Long customerId;
    private String customerFullName;
    private Long roomId;
    private String roomNumber;
    private String roomTypeDisplayName;
    private BigDecimal pricePerNight;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int numberOfGuests;
    private long nights;
    private BigDecimal totalPrice;
}
