package com.backend1.backend1.controller;

import com.backend1.backend1.form.SearchForm;
import com.backend1.backend1.service.BookingService;
import com.backend1.backend1.service.CustomerService;
import com.backend1.backend1.service.RoomService;
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

    private final BookingService bookingService;
    private final CustomerService customerService;
    private final RoomService roomService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("bookings", bookingService.findAll());
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
        model.addAttribute("rooms", roomService.findAll());
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
            bookingService.save(null, customerId, roomId, checkIn, checkOut, numberOfGuests);
            redirectAttributes.addFlashAttribute("successMessage", "Bokningen skapades.");
            return "redirect:/bookings";
        } catch (IllegalArgumentException e) {
            return bookingFormWithError(e.getMessage(), null, customerId, roomId, checkIn, checkOut, numberOfGuests, model);
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        var b = bookingService.findById(id);
        model.addAttribute("bookingId", id);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("selectedCustomerId", b.getCustomerId());
        model.addAttribute("selectedRoomId", b.getRoomId());
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
            bookingService.save(id, customerId, roomId, checkIn, checkOut, numberOfGuests);
            redirectAttributes.addFlashAttribute("successMessage", "Bokningen uppdaterades.");
            return "redirect:/bookings";
        } catch (IllegalArgumentException e) {
            return bookingFormWithError(e.getMessage(), id, customerId, roomId, checkIn, checkOut, numberOfGuests, model);
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookingService.delete(id);
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
        var available = roomService.findAvailableByDates(searchForm.getCheckIn(), searchForm.getCheckOut())
                .stream()
                .filter(r -> r.getCapacity() >= guests)
                .toList();
        model.addAttribute("availableRooms", available);
        model.addAttribute("checkIn", searchForm.getCheckIn());
        model.addAttribute("checkOut", searchForm.getCheckOut());
        model.addAttribute("numberOfGuests", guests);
        return "bookings/search";
    }

    private String bookingFormWithError(String error, Long bookingId, Long customerId, Long roomId,
                                        LocalDate checkIn, LocalDate checkOut, int guests, Model model) {
        model.addAttribute("errorMessage", error);
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("selectedCustomerId", customerId);
        model.addAttribute("selectedRoomId", roomId);
        model.addAttribute("selectedCheckIn", checkIn);
        model.addAttribute("selectedCheckOut", checkOut);
        model.addAttribute("selectedGuests", guests);
        model.addAttribute("pageTitle", bookingId == null ? "Ny bokning" : "Redigera bokning");
        return "bookings/form";
    }
}
