package com.example.redemptionservice.compensation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RedemptionCompensationRepository
        extends JpaRepository<RedemptionCompensation, Long> {
    boolean existsByEventId(UUID eventId);
    boolean existsByRedemptionId(Long redemptionId);
}
