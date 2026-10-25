package com.example.fulfillmentservice.event;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class RedemptionFailedEvent {
    UUID eventId;
    Instant occurredAt;
    Long redemptionId;
    Long customerId;
    Long pointsToReturn;
    String reason;
}
