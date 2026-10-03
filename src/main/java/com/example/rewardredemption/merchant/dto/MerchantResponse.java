package com.example.rewardredemption.merchant.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class MerchantResponse {
    private Long id;
    private String name;
    private String apiBaseUrl;
    private Boolean active;
    private Instant createdAt;
}
