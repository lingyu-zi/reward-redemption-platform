package com.example.rewardredemption.redemption.dto;

import lombok.Data;

@Data
public class RedemptionItemResponse {
    private Long id;
    private Long rewardId;
    private String rewardName;
    private Integer quantity;
    private Long pointsCost;
}
