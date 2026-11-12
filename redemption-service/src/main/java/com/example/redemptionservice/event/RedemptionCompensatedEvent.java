package com.example.redemptionservice.event;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class RedemptionCompensatedEvent {
    private UUID eventId;
    private Instant occurredAt;

    private Long redemptionId;
    private Long customerId;

    private Long pointsReturned;
    private Long remainingPointsBalance;

    private String reason;
}
