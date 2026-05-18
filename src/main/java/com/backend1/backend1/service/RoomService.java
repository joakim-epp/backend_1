package com.backend1.backend1.service;

import com.backend1.backend1.form.RoomForm;
import com.backend1.backend1.model.Room;
import com.backend1.backend1.model.RoomType;
import com.backend1.backend1.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public List<RoomForm> findAll() {
        return roomRepository.findAll()
                .stream()
                .map(this::toForm)
                .toList();
    }

    public RoomForm findById(Long id) {
        return toForm(roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rum saknas: " + id)));
    }

    public List<RoomForm> findAvailableByDates(LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAvailableByDates(checkIn, checkOut)
                .stream()
                .map(this::toForm)
                .toList();
    }

    public void save(RoomForm form) {
        Room r = form.getId() != null
                ? roomRepository.findById(form.getId())
                .orElseThrow(() -> new RuntimeException("Rum saknas"))
                : new Room();

        r.setRoomNumber(form.getRoomNumber());
        r.setType(form.getType());

        r.setExtraBeds(form.getType() == RoomType.SINGLE ? 0 : form.getExtraBeds());
        r.setPricePerNight(form.getPricePerNight());
        roomRepository.save(r);
    }

    public void delete(Long id) {
        roomRepository.deleteById(id);
    }

    public long count() {
        return roomRepository.count();
    }

    private RoomForm toForm(Room r) {
        RoomForm f = new RoomForm();
        f.setId(r.getId());
        f.setRoomNumber(r.getRoomNumber());
        f.setType(r.getType());
        f.setExtraBeds(r.getExtraBeds());
        f.setPricePerNight(r.getPricePerNight());
        return f;
    }
}