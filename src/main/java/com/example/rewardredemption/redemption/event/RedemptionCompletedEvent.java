package com.example.rewardredemption.redemption.event;

import lombok.Data;

import java.util.List;

@Data
public class RedemptionCompletedEvent {
    private Long redemptionId;
    private Long customerId;
    private List<Long> rewardIds;
    private Long pointsChange;
    private Long remainingPointsBalance;
}
