package com.backend1.backend1.service;

import com.backend1.backend1.dto.RoomDTO;
import com.backend1.backend1.form.RoomForm;
import com.backend1.backend1.model.Room;
import com.backend1.backend1.model.RoomType;
import com.backend1.backend1.repository.BookingRepository;
import com.backend1.backend1.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public List<RoomDTO> findAll() {
        return roomRepository.findAll().stream().map(this::toDTO).toList();
    }

    public RoomDTO findById(Long id) {
        return roomRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Rum med id " + id + " hittades inte"));
    }

    public void save(RoomForm form) {
        if (form.getType() == RoomType.SINGLE) form.setExtraBeds(0);
        roomRepository.save(toEntity(form));
    }

    public void delete(Long id) {
        roomRepository.deleteById(id);
    }

    public long count() {
        return roomRepository.count();
    }

    public List<RoomDTO> findAvailableByDates(LocalDate checkIn, LocalDate checkOut) {
        List<Long> bookedIds = bookingRepository
                .findByCheckInBeforeAndCheckOutAfter(checkOut, checkIn)
                .stream().map(b -> b.getRoom().getId()).toList();
        List<Room> rooms = bookedIds.isEmpty()
                ? roomRepository.findAll()
                : roomRepository.findByIdNotIn(bookedIds);
        return rooms.stream().map(this::toDTO).toList();
    }

    private RoomDTO toDTO(Room r) {
        RoomDTO dto = new RoomDTO();
        dto.setId(r.getId());
        dto.setRoomNumber(r.getRoomNumber());
        dto.setType(r.getType());
        dto.setExtraBeds(r.getExtraBeds());
        dto.setPricePerNight(r.getPricePerNight());
        dto.setCapacity(r.getCapacity());
        dto.setTypeDescription(buildTypeDescription(r));
        return dto;
    }

    private static String buildTypeDescription(Room r) {
        if (r.getType() == RoomType.SINGLE) return "Enkelrum (1 person)";
        String extra = switch (r.getExtraBeds()) {
            case 1 -> "1 extrasäng";
            case 2 -> "2 extrasängar";
            default -> "inga extrasängar";
        };
        return "Dubbelrum, " + extra + " (max " + r.getCapacity() + " pers.)";
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
