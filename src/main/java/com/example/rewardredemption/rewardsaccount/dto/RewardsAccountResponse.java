package com.example.rewardredemption.rewardsaccount.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class RewardsAccountResponse {
    private Long id;
    private Long customerId;
    private Long pointsBalance;
    private Instant createdAt;
}
