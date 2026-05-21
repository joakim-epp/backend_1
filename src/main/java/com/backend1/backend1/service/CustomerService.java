package com.backend1.backend1.service;

import com.backend1.backend1.dto.CustomerDTO;
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

    public List<CustomerDTO> findAll() {
        return customerRepository.findAll().stream().map(this::toDTO).toList();
    }

    public CustomerDTO findById(Long id) {
        return customerRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Kund med id " + id + " hittades inte"));
    }

    public void save(CustomerForm form) {
        customerRepository.save(toEntity(form));
    }

    public void delete(Long id) {
        if (bookingRepository.existsByCustomerId(id)) {
            throw new IllegalStateException("Kan inte ta bort kund som har aktiva bokningar");
        }
        customerRepository.deleteById(id);
    }

    public long count() {
        return customerRepository.count();
    }

    private CustomerDTO toDTO(Customer c) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(c.getId());
        dto.setFirstName(c.getFirstName());
        dto.setLastName(c.getLastName());
        dto.setEmail(c.getEmail());
        dto.setPhone(c.getPhone());
        dto.setAddress(c.getAddress());
        dto.setFullName(c.getFirstName() + " " + c.getLastName());
        dto.setBookingCount(bookingRepository.countByCustomerId(c.getId()));
        return dto;
    }

    private Customer toEntity(CustomerForm form) {
        Customer c = new Customer();
        c.setId(form.getId());
        c.setFirstName(form.getFirstName());
        c.setLastName(form.getLastName());
        c.setEmail(form.getEmail());
        c.setPhone(form.getPhone());
        c.setAddress(form.getAddress());
        return c;
    }
}
