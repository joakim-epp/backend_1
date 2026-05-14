package com.backend1.backend1.service;

import com.backend1.backend1.form.RoomForm;
import com.backend1.backend1.model.Room;
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
        return roomRepository.findAll().stream().map(this::toForm).toList();
    }

    public RoomForm findById(Long id) {
        return roomRepository.findById(id)
                .map(this::toForm)
                .orElseThrow(() -> new IllegalArgumentException("Rum med id " + id + " hittades inte"));
    }

    public void save(RoomForm form) {
        roomRepository.save(toEntity(form));
    }

    public void delete(Long id) {
        roomRepository.deleteById(id);
    }

    public List<RoomForm> findAvailableByDates(LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAvailableByDates(checkIn, checkOut).stream().map(this::toForm).toList();
    }

    private RoomForm toForm(Room r) {
        RoomForm form = new RoomForm();
        form.setId(r.getId());
        form.setRoomNumber(r.getRoomNumber());
        form.setType(r.getType());
        form.setExtraBeds(r.getExtraBeds());
        form.setPricePerNight(r.getPricePerNight());
        return form;
    }

    private Room toEntity(RoomForm form) {
        Room r = new Room();
        r.setId(form.getId());
        r.setRoomNumber(form.getRoomNumber());
        r.setType(form.getType());
        r.setExtraBeds(form.getExtraBeds());
        r.setPricePerNight(form.getPricePerNight());
        return r;
    }
}
