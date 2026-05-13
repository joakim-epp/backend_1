package com.backend1.backend1.model;

import java.math.BigDecimal;

public class Room {

    private Long id;
    private String roomNumber;
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public int getExtraBeds() { return extraBeds; }
    public void setExtraBeds(int extraBeds) { this.extraBeds = extraBeds; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
}
