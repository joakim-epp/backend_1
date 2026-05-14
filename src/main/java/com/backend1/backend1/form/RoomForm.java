package com.backend1.backend1.form;

import com.backend1.backend1.model.RoomType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomForm {
    private Long id;
    private String roomNumber;
    private RoomType type;
    private int extraBeds;
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
