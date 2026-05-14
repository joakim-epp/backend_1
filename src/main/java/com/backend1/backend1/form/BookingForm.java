package com.backend1.backend1.form;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
public class BookingForm {
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

    public long getNights() {
        if (checkIn == null || checkOut == null) return 0;
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public BigDecimal getTotalPrice() {
        if (pricePerNight == null) return BigDecimal.ZERO;
        return pricePerNight.multiply(BigDecimal.valueOf(getNights()));
    }
}
