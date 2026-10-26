package com.example.redemptionservice.event;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class RedemptionFailedEvent {
    private UUID eventId;
    private Instant occurredAt;
    private Long redemptionId;
    private Long customerId;
    private Long pointsToReturn;
    private String reason;
}
