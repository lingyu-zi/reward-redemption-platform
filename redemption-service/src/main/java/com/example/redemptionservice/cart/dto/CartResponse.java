package com.example.redemptionservice.cart.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class CartResponse {
    private Long id;
    private Long customerId;
    private Instant createdAt;
    private List<CartItemResponse> items;
    private Long totalPoints;
}
