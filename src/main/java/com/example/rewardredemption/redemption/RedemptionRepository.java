package com.example.rewardredemption.redemption;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RedemptionRepository extends JpaRepository<Redemption, Long> {
    List<Redemption> findByCustomerId(Long customerId);
}