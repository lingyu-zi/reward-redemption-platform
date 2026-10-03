package com.example.rewardredemption.reward.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class RewardResponse {
    private Long id;
    private String name;
    private String description;
    private Long pointsCost;

    private Long categoryId;
    private String categoryName;

    private Long merchantId;
    private String merchantName;

    private Boolean active;
    private Instant createdAt;
}
