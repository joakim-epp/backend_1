package com.backend1.backend1.controller;

import com.backend1.backend1.config.Store;
import com.backend1.backend1.model.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final Store store;

    public CustomerController(Store store) {
        this.store = store;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("customers", store.findAllCustomers());
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
        store.saveCustomer(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Kunden skapades.");
        return "redirect:/customers";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("customer", store.findCustomerById(id));
        model.addAttribute("pageTitle", "Redigera kund");
        return "customers/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Customer customer,
                         RedirectAttributes redirectAttributes) {
        customer.setId(id);
        store.saveCustomer(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Kundens uppgifter uppdaterades.");
        return "redirect:/customers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            store.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("successMessage", "Kunden togs bort.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/customers";
    }
}
