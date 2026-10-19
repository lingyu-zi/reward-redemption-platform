package com.example.redemptionservice.outbox;

@lombok.Getter
@lombok.Setter@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "redemption_outbox_events", schema = "reward_redemption")
public class RedemptionOutboxEvent {
@jakarta.persistence.Id
@jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
@jakarta.persistence.Column(name = "id", nullable = false)
private java.lang.Long id;

@jakarta.validation.constraints.Size(max = 16)
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "event_id", nullable = false, length = 16)
private java.lang.String eventId;

@jakarta.validation.constraints.Size(max = 100)
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "event_type", nullable = false, length = 100)
private java.lang.String eventType;

@jakarta.validation.constraints.NotNull
@jakarta.persistence.Lob
@jakarta.persistence.Column(name = "payload", nullable = false)
private java.lang.String payload;

@jakarta.validation.constraints.NotNull
@org.hibernate.annotations.ColumnDefault("0")
@jakarta.persistence.Column(name = "published", nullable = false)
private java.lang.Boolean published;

@jakarta.validation.constraints.NotNull
@org.hibernate.annotations.ColumnDefault("CURRENT_TIMESTAMP(6)")
@jakarta.persistence.Column(name = "created_at", nullable = false)
private java.time.Instant createdAt;

@jakarta.persistence.Column(name = "published_at")
private java.time.Instant publishedAt;



}