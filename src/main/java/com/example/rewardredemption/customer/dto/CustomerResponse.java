package com.example.rewardredemption.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Instant createdAt;
}
