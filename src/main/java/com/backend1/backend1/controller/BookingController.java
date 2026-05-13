package com.backend1.backend1.controller;

import com.backend1.backend1.config.Store;
import com.backend1.backend1.form.SearchForm;
import com.backend1.backend1.model.Booking;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final Store store;

    public BookingController(Store store) {
        this.store = store;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("bookings", store.findAllBookings());
        return "bookings/list";
    }

    @GetMapping("/new")
    public String showCreateForm(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false, defaultValue = "1") int guests,
            Model model) {
        model.addAttribute("customers", store.findAllCustomers());
        model.addAttribute("rooms", store.findAllRooms());
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
            store.saveBooking(buildBooking(null, customerId, roomId, checkIn, checkOut, numberOfGuests));
            redirectAttributes.addFlashAttribute("successMessage", "Bokningen skapades.");
            return "redirect:/bookings";
        } catch (IllegalArgumentException e) {
            return bookingFormWithError(e.getMessage(), null, customerId, roomId, checkIn, checkOut, numberOfGuests, model);
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Booking b = store.findBookingById(id);
        model.addAttribute("bookingId", id);
        model.addAttribute("customers", store.findAllCustomers());
        model.addAttribute("rooms", store.findAllRooms());
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
            store.saveBooking(buildBooking(id, customerId, roomId, checkIn, checkOut, numberOfGuests));
            redirectAttributes.addFlashAttribute("successMessage", "Bokningen uppdaterades.");
            return "redirect:/bookings";
        } catch (IllegalArgumentException e) {
            return bookingFormWithError(e.getMessage(), id, customerId, roomId, checkIn, checkOut, numberOfGuests, model);
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        store.deleteBooking(id);
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
        model.addAttribute("availableRooms", store.findAvailableRooms(
                searchForm.getCheckIn(), searchForm.getCheckOut(), searchForm.getNumberOfGuests()));
        model.addAttribute("checkIn", searchForm.getCheckIn());
        model.addAttribute("checkOut", searchForm.getCheckOut());
        model.addAttribute("numberOfGuests", searchForm.getNumberOfGuests());
        return "bookings/search";
    }

    private Booking buildBooking(Long id, Long customerId, Long roomId,
                                  LocalDate checkIn, LocalDate checkOut, int guests) {
        Booking b = new Booking();
        b.setId(id);
        b.setCustomer(store.findCustomerById(customerId));
        b.setRoom(store.findRoomById(roomId));
        b.setCheckIn(checkIn);
        b.setCheckOut(checkOut);
        b.setNumberOfGuests(guests);
        return b;
    }

    private String bookingFormWithError(String error, Long bookingId, Long customerId, Long roomId,
                                         LocalDate checkIn, LocalDate checkOut, int guests, Model model) {
        model.addAttribute("errorMessage", error);
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("customers", store.findAllCustomers());
        model.addAttribute("rooms", store.findAllRooms());
        model.addAttribute("selectedCustomerId", customerId);
        model.addAttribute("selectedRoomId", roomId);
        model.addAttribute("selectedCheckIn", checkIn);
        model.addAttribute("selectedCheckOut", checkOut);
        model.addAttribute("selectedGuests", guests);
        model.addAttribute("pageTitle", bookingId == null ? "Ny bokning" : "Redigera bokning");
        return "bookings/form";
    }
}
