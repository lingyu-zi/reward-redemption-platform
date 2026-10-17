package com.example.redemptionservice.event;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class RedemptionCompletedEvent {
    private UUID eventId;
    private Long redemptionId;
    private Long customerId;
    private List<Long> rewardIds;
    private Long pointsChange;
    private Long remainingPointsBalance;
    private Instant occurredAt;
}
