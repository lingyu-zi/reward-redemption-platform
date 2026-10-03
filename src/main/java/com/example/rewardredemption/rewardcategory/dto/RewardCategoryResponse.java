package com.example.rewardredemption.rewardcategory.dto;

import lombok.Data;
import java.time.Instant;


@Data
public class RewardCategoryResponse {
    private Long id;
    private String name;
    private Instant createdAt;
}
