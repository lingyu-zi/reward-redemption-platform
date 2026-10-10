package com.example.redemptionservice.cart.dto;

import lombok.Data;

@Data
public class CartItemResponse {
    private Long id;
    private Long rewardId;
    private String rewardName;
    private Long pointsCost;
    private Integer quantity;
    private Long totalPoints;
}
