package com.backend1.backend1.service;

import com.backend1.backend1.dto.RoomDTO;
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

    private static String buildTypeDescription(Room r) {
        if (r.getType() == RoomType.SINGLE) return "Enkelrum (1 person)";
        String extra = switch (r.getExtraBeds()) {
            case 1 -> "1 extrasäng";
            case 2 -> "2 extrasängar";
            default -> "inga extrasängar";
        };
        return "Dubbelrum, " + extra + " (max " + r.getCapacity() + " pers.)";
    }

    public List<RoomForm> findAll() {
        return roomRepository.findAll().stream().map(this::toDTO).map(this::toForm).toList();
    }

    public RoomForm findById(Long id) {
        return roomRepository.findById(id)
                .map(this::toDTO)
                .map(this::toForm)
                .orElseThrow(() -> new IllegalArgumentException("Rum med id " + id + " hittades inte"));
    }

    public void save(RoomForm form) {
        if (form.getType() == RoomType.SINGLE) form.setExtraBeds(0);
        roomRepository.save(toEntity(form));
    }

    public void delete(Long id) {
        roomRepository.deleteById(id);
    }

    public List<RoomForm> findAvailableByDates(LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAvailableByDates(checkIn, checkOut).stream().map(this::toDTO).map(this::toForm).toList();
    }

    private RoomDTO toDTO(Room r) {
        return new RoomDTO(
                r.getId(),
                r.getRoomNumber(),
                r.getType(),
                r.getExtraBeds(),
                r.getPricePerNight(),
                r.getCapacity(),
                buildTypeDescription(r)
        );
    }

    private RoomForm toForm(RoomDTO dto) {
        RoomForm form = new RoomForm();
        form.setId(dto.id());
        form.setRoomNumber(dto.roomNumber());
        form.setType(dto.type());
        form.setExtraBeds(dto.extraBeds());
        form.setPricePerNight(dto.pricePerNight());
        form.setCapacity(dto.capacity());
        form.setTypeDescription(dto.typeDescription());
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
