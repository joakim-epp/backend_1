package com.backend1.backend1.config;

import com.backend1.backend1.model.Booking;
import com.backend1.backend1.model.Customer;
import com.backend1.backend1.model.Room;
import com.backend1.backend1.model.RoomType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Store {

    private long customerIdSeq = 1;
    private long roomIdSeq = 1;
    private long bookingIdSeq = 1;

    private final List<Customer> customers = new ArrayList<>();
    private final List<Room> rooms = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    @PostConstruct
    public void init() {
        saveRoom(room("101", RoomType.SINGLE, 0, "800"));
        saveRoom(room("102", RoomType.SINGLE, 0, "800"));
        saveRoom(room("103", RoomType.SINGLE, 0, "850"));
        saveRoom(room("201", RoomType.DOUBLE, 0, "1200"));
        saveRoom(room("202", RoomType.DOUBLE, 1, "1400"));
        saveRoom(room("203", RoomType.DOUBLE, 2, "1600"));
        saveRoom(room("301", RoomType.DOUBLE, 0, "1500"));
        saveRoom(room("302", RoomType.DOUBLE, 2, "1800"));
    }

    // ---- Customers ----

    public List<Customer> findAllCustomers() {
        customers.forEach(c -> c.setBookings(
                bookings.stream()
                        .filter(b -> b.getCustomer().getId().equals(c.getId()))
                        .collect(Collectors.toList())));
        return new ArrayList<>(customers);
    }

    public Customer findCustomerById(Long id) {
        return customers.stream().filter(c -> c.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Kund med id " + id + " hittades inte"));
    }

    public void saveCustomer(Customer c) {
        if (c.getId() == null) {
            c.setId(customerIdSeq++);
            customers.add(c);
        } else {
            customers.replaceAll(x -> x.getId().equals(c.getId()) ? c : x);
        }
    }

    public void deleteCustomer(Long id) {
        if (bookings.stream().anyMatch(b -> b.getCustomer().getId().equals(id))) {
            throw new IllegalStateException("Kan inte ta bort kund som har aktiva bokningar");
        }
        customers.removeIf(c -> c.getId().equals(id));
    }

    // ---- Rooms ----

    public List<Room> findAllRooms() {
        return new ArrayList<>(rooms);
    }

    public Room findRoomById(Long id) {
        return rooms.stream().filter(r -> r.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Rum med id " + id + " hittades inte"));
    }

    public void saveRoom(Room r) {
        if (r.getType() == RoomType.SINGLE) r.setExtraBeds(0);
        if (r.getId() == null) {
            r.setId(roomIdSeq++);
            rooms.add(r);
        } else {
            rooms.replaceAll(x -> x.getId().equals(r.getId()) ? r : x);
        }
    }

    public void deleteRoom(Long id) {
        rooms.removeIf(r -> r.getId().equals(id));
    }

    // ---- Bookings ----

    public List<Booking> findAllBookings() {
        return new ArrayList<>(bookings);
    }

    public Booking findBookingById(Long id) {
        return bookings.stream().filter(b -> b.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Bokning med id " + id + " hittades inte"));
    }

    public void saveBooking(Booking b) {
        if (!b.getCheckOut().isAfter(b.getCheckIn())) {
            throw new IllegalArgumentException("Utcheckningsdatum måste vara efter incheckningsdatum");
        }
        if (b.getRoom().getCapacity() < b.getNumberOfGuests()) {
            throw new IllegalArgumentException("Rummet har plats för " + b.getRoom().getCapacity()
                    + " person(er), inte " + b.getNumberOfGuests());
        }
        boolean overlap = bookings.stream()
                .filter(x -> b.getId() == null || !x.getId().equals(b.getId()))
                .anyMatch(x -> x.getRoom().getId().equals(b.getRoom().getId())
                        && x.getCheckIn().isBefore(b.getCheckOut())
                        && x.getCheckOut().isAfter(b.getCheckIn()));
        if (overlap) {
            throw new IllegalArgumentException("Rummet är redan bokat för de valda datumen");
        }
        if (b.getId() == null) {
            b.setId(bookingIdSeq++);
            bookings.add(b);
        } else {
            bookings.replaceAll(x -> x.getId().equals(b.getId()) ? b : x);
        }
    }

    public void deleteBooking(Long id) {
        bookings.removeIf(b -> b.getId().equals(id));
    }

    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, int guests) {
        return rooms.stream()
                .filter(r -> r.getCapacity() >= guests)
                .filter(r -> bookings.stream().noneMatch(b ->
                        b.getRoom().getId().equals(r.getId())
                                && b.getCheckIn().isBefore(checkOut)
                                && b.getCheckOut().isAfter(checkIn)))
                .collect(Collectors.toList());
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
