package com.example.redemptionservice.compensation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "redemption_compensations")
public class RedemptionCompensation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "event_id", nullable = false, length = 16)
    private UUID eventId;

    @Column(name = "redemption_id", nullable = false)
    private Long redemptionId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "points_returned", nullable = false)
    private Long pointsReturned;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private CompensationStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}