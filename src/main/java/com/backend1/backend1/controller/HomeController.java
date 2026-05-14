package com.backend1.backend1.controller;

import com.backend1.backend1.repository.BookingRepository;
import com.backend1.backend1.repository.CustomerRepository;
import com.backend1.backend1.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("customerCount", customerRepository.count());
        model.addAttribute("roomCount", roomRepository.count());
        model.addAttribute("bookingCount", bookingRepository.count());
        return "index";
    }
}
