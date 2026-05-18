package com.backend1.backend1.controller;

import com.backend1.backend1.form.RoomForm;
import com.backend1.backend1.model.RoomType;
import com.backend1.backend1.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rooms", roomService.findAll());
        return "rooms/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("room", new RoomForm());
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("pageTitle", "Nytt rum");
        return "rooms/form";
    }

    @PostMapping
    public String create(@ModelAttribute RoomForm room, RedirectAttributes redirectAttributes) {
        roomService.save(room);
        redirectAttributes.addFlashAttribute("successMessage", "Rummet skapades.");
        return "redirect:/rooms";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("room", roomService.findById(id));
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("pageTitle", "Redigera rum");
        return "rooms/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute RoomForm room,
                         RedirectAttributes redirectAttributes) {
        room.setId(id);
        roomService.save(room);
        redirectAttributes.addFlashAttribute("successMessage", "Rummet uppdaterades.");
        return "redirect:/rooms";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        roomService.delete(id);

        redirectAttributes.addFlashAttribute("successMessage", "Rummet togs bort.");
        return "redirect:/rooms";
    }
}
