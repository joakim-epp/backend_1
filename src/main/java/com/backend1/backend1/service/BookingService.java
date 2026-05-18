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
        return bookingRepository.findAll()
                .stream()
                .map(this::toForm)
                .toList();
    }

    public BookingForm findById(Long id) {
        return toForm(bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bokning saknas: " + id)));
    }

    public void save(Long bookingId, Long customerId, Long roomId,
                     LocalDate checkIn, LocalDate checkOut, int numberOfGuests) {

        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Utcheckningsdatum måste vara efter incheckningsdatum.");
        }

        // Kolla att rummet är ledigt (exkludera nuvarande bokning vid uppdatering)
        List<Booking> conflicts = bookingRepository.findConflicting(roomId, checkIn, checkOut, bookingId);
        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Rummet är redan bokat för de valda datumen.");
        }

        Booking booking = bookingId != null
                ? bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Bokning saknas"))
                : new Booking();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Kund saknas"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Rum saknas"));

        // Kolla att kapaciteten räcker
        int capacity = room.getType().name().equals("SINGLE") ? 1 : 2 + room.getExtraBeds();
        if (numberOfGuests > capacity) {
            throw new IllegalArgumentException(
                    "Rummet rymmer max " + capacity + " gäster.");
        }

        booking.setCustomer(customer);
        booking.setRoom(room);
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setNumberOfGuests(numberOfGuests);
        bookingRepository.save(booking);
    }

    public void delete(Long id) {
        bookingRepository.deleteById(id);
    }

    public long count() {
        return bookingRepository.count();
    }

    private BookingForm toForm(Booking b) {
        BookingForm f = new BookingForm();
        f.setId(b.getId());
        f.setCustomerId(b.getCustomer().getId());
        f.setCustomerFullName(b.getCustomer().getFullName());
        f.setRoomId(b.getRoom().getId());
        f.setRoomNumber(b.getRoom().getRoomNumber());
        f.setRoomTypeDisplayName(b.getRoom().getType().getDisplayName());
        f.setPricePerNight(b.getRoom().getPricePerNight());
        f.setCheckIn(b.getCheckIn());
        f.setCheckOut(b.getCheckOut());
        f.setNumberOfGuests(b.getNumberOfGuests());
        return f;
    }
}
