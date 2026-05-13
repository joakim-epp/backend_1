package com.backend1.backend1.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class SearchForm {

    @NotNull(message = "Incheckning är obligatorisk")
    private LocalDate checkIn;

    @NotNull(message = "Utcheckning är obligatorisk")
    private LocalDate checkOut;

    @Min(value = 1, message = "Minst 1 gäst krävs")
    private int numberOfGuests = 1;

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public int getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(int numberOfGuests) { this.numberOfGuests = numberOfGuests; }
}
