package com.example.rewardredemption.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedemptionCartItemRepository extends JpaRepository<RedemptionCartItem, Long> {
    Optional<RedemptionCartItem> findByCartIdAndRewardId(Long cartId, Long rewardId);
    Optional<RedemptionCartItem> findByCartIdAndId(Long cartId, Long itemId);
}