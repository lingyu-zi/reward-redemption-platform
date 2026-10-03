package com.example.rewardredemption.merchant;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    boolean existsByName(String name);
}