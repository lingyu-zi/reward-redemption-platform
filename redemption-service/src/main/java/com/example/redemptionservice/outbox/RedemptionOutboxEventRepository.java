package com.example.redemptionservice.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RedemptionOutboxEventRepository
        extends JpaRepository<RedemptionOutboxEvent, Long> {
    List<RedemptionOutboxEvent> findByPublishedFalseOrderByCreatedAtAsc();
}
