package com.example.redemptionservice.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RewardsAccountRepository extends JpaRepository<RewardsAccount, Long> {
    Optional<RewardsAccount> findByCustomerId(Long customerId);
    boolean existsByCustomerId(Long customerId);
}
