package com.example.rewardredemption.customer;

import com.example.rewardredemption.customer.dto.CreateCustomerRequest;
import com.example.rewardredemption.customer.dto.CustomerResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomer(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PostMapping
    public CustomerResponse createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        return customerService.createCustomer(request);
    }

}
