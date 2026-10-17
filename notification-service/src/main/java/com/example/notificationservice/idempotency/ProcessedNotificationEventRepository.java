package com.example.notificationservice.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedNotificationEventRepository
        extends JpaRepository<ProcessedNotificationEvent, Long> {

    boolean existsByEventId(UUID eventId);
}
