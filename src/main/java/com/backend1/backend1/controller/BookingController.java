package com.backend1.backend1.controller;

import com.backend1.backend1.form.SearchForm;
import com.backend1.backend1.model.Booking;
import com.backend1.backend1.model.Customer;
import com.backend1.backend1.model.Room;
import com.backend1.backend1.repository.BookingRepository;
import com.backend1.backend1.repository.CustomerRepository;
import com.backend1.backend1.repository.RoomRepository;
import com.backend1.backend1.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;
    private final CustomerService customerService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("bookings", bookingRepository.findAll());
        return "bookings/list";
    }

    @GetMapping("/new")
    public String showCreateForm(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false, defaultValue = "1") int guests,
            Model model) {
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("rooms", roomRepository.findAll());
        model.addAttribute("selectedRoomId", roomId);
        model.addAttribute("selectedCheckIn", checkIn);
        model.addAttribute("selectedCheckOut", checkOut);
        model.addAttribute("selectedGuests", guests);
        model.addAttribute("pageTitle", "Ny bokning");
        return "bookings/form";
    }

    @PostMapping
    public String create(
            @RequestParam Long customerId,
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "1") int numberOfGuests,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            bookingRepository.save(buildAndValidate(null, customerId, roomId, checkIn, checkOut, numberOfGuests));
            redirectAttributes.addFlashAttribute("successMessage", "Bokningen skapades.");
            return "redirect:/bookings";
        } catch (IllegalArgumentException e) {
            return bookingFormWithError(e.getMessage(), null, customerId, roomId, checkIn, checkOut, numberOfGuests, model);
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Booking b = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bokning med id " + id + " hittades inte"));
        model.addAttribute("bookingId", id);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("rooms", roomRepository.findAll());
        model.addAttribute("selectedCustomerId", b.getCustomer().getId());
        model.addAttribute("selectedRoomId", b.getRoom().getId());
        model.addAttribute("selectedCheckIn", b.getCheckIn());
        model.addAttribute("selectedCheckOut", b.getCheckOut());
        model.addAttribute("selectedGuests", b.getNumberOfGuests());
        model.addAttribute("pageTitle", "Redigera bokning");
        return "bookings/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam Long customerId,
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "1") int numberOfGuests,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            bookingRepository.save(buildAndValidate(id, customerId, roomId, checkIn, checkOut, numberOfGuests));
            redirectAttributes.addFlashAttribute("successMessage", "Bokningen uppdaterades.");
            return "redirect:/bookings";
        } catch (IllegalArgumentException e) {
            return bookingFormWithError(e.getMessage(), id, customerId, roomId, checkIn, checkOut, numberOfGuests, model);
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookingRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Bokningen avbokades.");
        return "redirect:/bookings";
    }

    @GetMapping("/search")
    public String showSearch(Model model) {
        model.addAttribute("searchForm", new SearchForm());
        return "bookings/search";
    }

    @PostMapping("/search")
    public String search(@Valid @ModelAttribute SearchForm searchForm,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) return "bookings/search";
        if (!searchForm.getCheckOut().isAfter(searchForm.getCheckIn())) {
            result.rejectValue("checkOut", "invalid", "Utcheckningsdatum måste vara efter incheckningsdatum");
            return "bookings/search";
        }
        int guests = searchForm.getNumberOfGuests();
        var available = roomRepository.findAvailableByDates(searchForm.getCheckIn(), searchForm.getCheckOut())
                .stream()
                .filter(r -> r.getCapacity() >= guests)
                .toList();
        model.addAttribute("availableRooms", available);
        model.addAttribute("checkIn", searchForm.getCheckIn());
        model.addAttribute("checkOut", searchForm.getCheckOut());
        model.addAttribute("numberOfGuests", guests);
        return "bookings/search";
    }

    private Booking buildAndValidate(Long id, Long customerId, Long roomId,
                                      LocalDate checkIn, LocalDate checkOut, int numberOfGuests) {
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Utcheckningsdatum måste vara efter incheckningsdatum");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Kund hittades inte"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Rum hittades inte"));
        if (room.getCapacity() < numberOfGuests) {
            throw new IllegalArgumentException("Rummet har plats för " + room.getCapacity()
                    + " person(er), inte " + numberOfGuests);
        }
        if (bookingRepository.countOverlap(roomId, checkIn, checkOut, id) > 0) {
            throw new IllegalArgumentException("Rummet är redan bokat för de valda datumen");
        }
        Booking b = new Booking();
        b.setId(id);
        b.setCustomer(customer);
        b.setRoom(room);
        b.setCheckIn(checkIn);
        b.setCheckOut(checkOut);
        b.setNumberOfGuests(numberOfGuests);
        return b;
    }

    private String bookingFormWithError(String error, Long bookingId, Long customerId, Long roomId,
                                         LocalDate checkIn, LocalDate checkOut, int guests, Model model) {
        model.addAttribute("errorMessage", error);
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("rooms", roomRepository.findAll());
        model.addAttribute("selectedCustomerId", customerId);
        model.addAttribute("selectedRoomId", roomId);
        model.addAttribute("selectedCheckIn", checkIn);
        model.addAttribute("selectedCheckOut", checkOut);
        model.addAttribute("selectedGuests", guests);
        model.addAttribute("pageTitle", bookingId == null ? "Ny bokning" : "Redigera bokning");
        return "bookings/form";
    }
}
