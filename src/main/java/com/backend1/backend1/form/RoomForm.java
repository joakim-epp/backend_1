package com.backend1.backend1.form;

import com.backend1.backend1.model.RoomType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomForm {
    private Long id;

    @NotBlank
    private String roomNumber;

    @NotNull
    private RoomType type;

    @Min(0)
    @Max(2)
    private int extraBeds;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal pricePerNight;

    private int capacity;
    private String typeDescription;
}
