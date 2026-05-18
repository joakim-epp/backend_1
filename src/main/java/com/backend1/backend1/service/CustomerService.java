package com.backend1.backend1.service;

import com.backend1.backend1.form.CustomerForm;
import com.backend1.backend1.model.Customer;
import com.backend1.backend1.repository.BookingRepository;
import com.backend1.backend1.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;

    public List<CustomerForm> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::toForm)
                .toList();
    }

    public CustomerForm findById(Long id) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kund saknas: " + id));
        return toForm(c);
    }

    public void save(CustomerForm form) {
        Customer c = form.getId() != null
                ? customerRepository.findById(form.getId())
                .orElseThrow(() -> new RuntimeException("Kund saknas"))
                : new Customer();

        c.setFirstName(form.getFirstName());
        c.setLastName(form.getLastName());
        c.setEmail(form.getEmail());
        c.setPhone(form.getPhone());
        c.setAddress(form.getAddress());
        customerRepository.save(c);
    }

    public void delete(Long id) {
        if (bookingRepository.existsByCustomerId(id)) {
            throw new IllegalStateException("Kan inte ta bort kund med aktiva bokningar.");
        }
        customerRepository.deleteById(id);
    }

    public long count() {
        return customerRepository.count();
    }

    private CustomerForm toForm(Customer c) {
        CustomerForm f = new CustomerForm();
        f.setId(c.getId());
        f.setFirstName(c.getFirstName());
        f.setLastName(c.getLastName());
        f.setEmail(c.getEmail());
        f.setPhone(c.getPhone());
        f.setAddress(c.getAddress());
        f.setBookingCount(c.getBookings().size());
        return f;
    }
}
