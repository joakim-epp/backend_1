package com.backend1.backend1.controller;

import com.backend1.backend1.config.Store;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final Store store;

    public HomeController(Store store) {
        this.store = store;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("customerCount", store.findAllCustomers().size());
        model.addAttribute("roomCount", store.findAllRooms().size());
        model.addAttribute("bookingCount", store.findAllBookings().size());
        return "index";
    }
}
