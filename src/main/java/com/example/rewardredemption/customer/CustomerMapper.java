package com.example.rewardredemption.customer;

import com.example.rewardredemption.customer.dto.CreateCustomerRequest;
import com.example.rewardredemption.customer.dto.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerResponse toResponse(Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Customer toEntity(CreateCustomerRequest request);
}
