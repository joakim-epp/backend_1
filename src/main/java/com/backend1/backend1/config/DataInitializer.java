package com.backend1.backend1.config;

import com.backend1.backend1.model.Room;
import com.backend1.backend1.model.RoomType;
import com.backend1.backend1.repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoomRepository roomRepository;

    @PostConstruct
    public void init() {
        if (roomRepository.count() > 0) return;
        roomRepository.save(room("101", RoomType.SINGLE, 0, "800"));
        roomRepository.save(room("102", RoomType.SINGLE, 0, "800"));
        roomRepository.save(room("103", RoomType.SINGLE, 0, "850"));
        roomRepository.save(room("201", RoomType.DOUBLE, 0, "1200"));
        roomRepository.save(room("202", RoomType.DOUBLE, 1, "1400"));
        roomRepository.save(room("203", RoomType.DOUBLE, 2, "1600"));
        roomRepository.save(room("301", RoomType.DOUBLE, 0, "1500"));
        roomRepository.save(room("302", RoomType.DOUBLE, 2, "1800"));
    }

    private Room room(String number, RoomType type, int extraBeds, String price) {
        Room r = new Room();
        r.setRoomNumber(number);
        r.setType(type);
        r.setExtraBeds(extraBeds);
        r.setPricePerNight(new BigDecimal(price));
        return r;
    }
}
