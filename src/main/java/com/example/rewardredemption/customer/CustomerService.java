package com.example.rewardredemption.customer;

import com.example.rewardredemption.customer.dto.CreateCustomerRequest;
import com.example.rewardredemption.customer.dto.CustomerResponse;
import com.example.rewardredemption.exception.DuplicateResourceException;
import com.example.rewardredemption.exception.ResourceNotFoundException;
import com.example.rewardredemption.rewardsaccount.RewardsAccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final RewardsAccountService rewardsAccountService;
    private final CustomerMapper customerMapper;


    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id " + id));
        return customerMapper.toResponse(customer);
    }

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Customer with email " + request.getEmail() + " already exists");
        }
        Customer customer = customerMapper.toEntity(request);
        customer.setRole("CUSTOMER");

        Customer savedCustomer = customerRepository.save(customer);
        rewardsAccountService.createAccount(savedCustomer);

        return customerMapper.toResponse(savedCustomer);
    }

}
