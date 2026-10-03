package com.example.rewardredemption.merchant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateMerchantRequest {
    @NotBlank(message = "Merchant name is required")
    @Size(max = 100, message = "Merchant name must not exceed 100 characters")
    private String name;

    @Size(max = 255,message = "API base URL must not exceed 255 characters")
    private String apiBaseUrl;
}
