package com.example.rewardredemption.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedemptionCartRepository extends JpaRepository<RedemptionCart, Long> {
    boolean existsByCustomerId(Long customerId);
}