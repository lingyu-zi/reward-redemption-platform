package com.example.fulfillmentservice.fulfillment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FulfillmentRecordRepository extends JpaRepository<FulfillmentRecord, Long> {
    Optional<FulfillmentRecord> findByRedemptionId(Long redemptionId);
}
