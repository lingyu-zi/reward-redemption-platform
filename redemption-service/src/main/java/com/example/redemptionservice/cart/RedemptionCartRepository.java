package com.example.redemptionservice.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedemptionCartRepository extends JpaRepository<RedemptionCart, Long> {
    boolean existsByCustomerId(Long customerId);
}