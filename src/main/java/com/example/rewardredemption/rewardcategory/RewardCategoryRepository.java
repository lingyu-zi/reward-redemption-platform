package com.example.rewardredemption.rewardcategory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardCategoryRepository extends JpaRepository<RewardCategory, Long> {
    boolean existsByName(String name);
}