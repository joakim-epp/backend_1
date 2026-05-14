package com.backend1.backend1.service;

import com.backend1.backend1.dto.CustomerDTO;
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

    public void save(CustomerDTO dto) {
        customerRepository.save(toEntity(dto));
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
        dto.setBookingCount(bookingRepository.countByCustomerId(c.getId()));
        return dto;
    }

    private Customer toEntity(CustomerDTO dto) {
        Customer c = new Customer();
        c.setId(dto.getId());
        c.setFirstName(dto.getFirstName());
        c.setLastName(dto.getLastName());
        c.setEmail(dto.getEmail());
        c.setPhone(dto.getPhone());
        c.setAddress(dto.getAddress());
        return c;
    }
}
