package com.example.rewardredemption.reward.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRewardRequest {
    @NotBlank(message = "Reward name is required")
    @Size(max = 150, message = "Reward name must not exceed 150 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Points cost is required")
    @Positive(message = "Points cost must be greater than 0")
    private Long pointsCost;

    @NotNull(message = "Category id is required")
    private Long categoryId;

    @NotNull(message = "Merchant id is required")
    private Long merchantId;
}
