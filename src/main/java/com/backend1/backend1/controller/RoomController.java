package com.backend1.backend1.controller;

import com.backend1.backend1.config.Store;
import com.backend1.backend1.model.Room;
import com.backend1.backend1.model.RoomType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final Store store;

    public RoomController(Store store) {
        this.store = store;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rooms", store.findAllRooms());
        return "rooms/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("pageTitle", "Nytt rum");
        return "rooms/form";
    }

    @PostMapping
    public String create(@ModelAttribute Room room, RedirectAttributes redirectAttributes) {
        store.saveRoom(room);
        redirectAttributes.addFlashAttribute("successMessage", "Rummet skapades.");
        return "redirect:/rooms";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("room", store.findRoomById(id));
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("pageTitle", "Redigera rum");
        return "rooms/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Room room,
                         RedirectAttributes redirectAttributes) {
        room.setId(id);
        store.saveRoom(room);
        redirectAttributes.addFlashAttribute("successMessage", "Rummet uppdaterades.");
        return "redirect:/rooms";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        store.deleteRoom(id);
        redirectAttributes.addFlashAttribute("successMessage", "Rummet togs bort.");
        return "redirect:/rooms";
    }
}
