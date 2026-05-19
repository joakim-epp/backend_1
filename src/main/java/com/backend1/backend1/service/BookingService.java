package com.backend1.backend1.service;

import com.backend1.backend1.form.BookingForm;
import com.backend1.backend1.model.Booking;
import com.backend1.backend1.model.Customer;
import com.backend1.backend1.model.Room;
import com.backend1.backend1.repository.BookingRepository;
import com.backend1.backend1.repository.CustomerRepository;
import com.backend1.backend1.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;

    public List<BookingForm> findAll() {
        return bookingRepository.findAll().stream().map(this::toForm).toList();
    }

    public BookingForm findById(Long id) {
        return bookingRepository.findById(id)
                .map(this::toForm)
                .orElseThrow(() -> new IllegalArgumentException("Bokning med id " + id + " hittades inte"));
    }

    public void save(Long bookingId, Long customerId, Long roomId,
                     LocalDate checkIn, LocalDate checkOut, int numberOfGuests) {
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Utcheckningsdatum måste vara efter incheckningsdatum");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Kund hittades inte"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Rum hittades inte"));
        if (room.getCapacity() < numberOfGuests) {
            int capacity = room.getCapacity();
            throw new IllegalArgumentException("Rummet har plats för " + capacity
                    + (capacity == 1 ? " person" : " personer") + ", inte " + numberOfGuests);
        }
        if (bookingRepository.countOverlap(roomId, checkIn, checkOut, bookingId) > 0) {
            throw new IllegalArgumentException("Rummet är redan bokat för de valda datumen");
        }
        Booking b = new Booking();
        b.setId(bookingId);
        b.setCustomer(customer);
        b.setRoom(room);
        b.setCheckIn(checkIn);
        b.setCheckOut(checkOut);
        b.setNumberOfGuests(numberOfGuests);
        bookingRepository.save(b);
    }

    public void delete(Long id) {
        bookingRepository.deleteById(id);
    }

    private BookingForm toForm(Booking b) {
        BookingForm form = new BookingForm();
        form.setId(b.getId());
        if (b.getCustomer() != null) {
            form.setCustomerId(b.getCustomer().getId());
            form.setCustomerFullName(b.getCustomer().getFullName());
        }
        if (b.getRoom() != null) {
            form.setRoomId(b.getRoom().getId());
            form.setRoomNumber(b.getRoom().getRoomNumber());
            form.setRoomTypeDisplayName(b.getRoom().getType() != null
                    ? b.getRoom().getType().getDisplayName() : "");
            form.setPricePerNight(b.getRoom().getPricePerNight());
        }
        form.setCheckIn(b.getCheckIn());
        form.setCheckOut(b.getCheckOut());
        form.setNumberOfGuests(b.getNumberOfGuests());
        return form;
    }
}
