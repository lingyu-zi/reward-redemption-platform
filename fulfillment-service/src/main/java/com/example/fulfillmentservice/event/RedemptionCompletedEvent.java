package com.example.fulfillmentservice.event;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class RedemptionCompletedEvent {
    UUID eventId;
    Instant occurredAt;
    Long customerId;
    Long redemptionId;
    Long pointsChange;
    Long remainingPointsBalance;
    List<Long> rewardIds;
}
