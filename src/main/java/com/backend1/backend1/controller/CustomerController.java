package com.backend1.backend1.controller;

import com.backend1.backend1.model.Customer;
import com.backend1.backend1.repository.BookingRepository;
import com.backend1.backend1.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        return "customers/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("pageTitle", "Ny kund");
        return "customers/form";
    }

    @PostMapping
    public String create(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        customerRepository.save(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Kunden skapades.");
        return "redirect:/customers";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("customer", customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Kund med id " + id + " hittades inte")));
        model.addAttribute("pageTitle", "Redigera kund");
        return "customers/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Customer customer,
                         RedirectAttributes redirectAttributes) {
        customer.setId(id);
        customerRepository.save(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Kundens uppgifter uppdaterades.");
        return "redirect:/customers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (bookingRepository.existsByCustomerId(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kan inte ta bort kund som har aktiva bokningar");
        } else {
            customerRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Kunden togs bort.");
        }
        return "redirect:/customers";
    }
}
