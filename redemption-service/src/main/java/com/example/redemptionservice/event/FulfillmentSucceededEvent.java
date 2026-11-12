package com.example.redemptionservice.event;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class FulfillmentSucceededEvent {
    private UUID eventId;
    private Instant occurredAt;
    private Long redemptionId;
    private Long customerId;
    private Long pointsChange;
    private Long remainingPointsBalance;
    private List<Long> rewardIds;
}
