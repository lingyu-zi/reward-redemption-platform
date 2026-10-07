package com.example.rewardredemption.points;

import com.example.rewardredemption.exception.ResourceNotFoundException;
import com.example.rewardredemption.rewardsaccount.RewardsAccountRepository;
import com.example.rewardredemption.transaction.event.TransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
@Service
public class PointsProcessingService {
    private final RewardsAccountRepository rewardsAccountRepository;

    @Transactional
    public void processTransaction(TransactionCompletedEvent event) {
        var customerId = event.getCustomerId();
        var rewardsAccount= rewardsAccountRepository.findByCustomerId(customerId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Rewards account not found for customer id: " + customerId));
        var points = event.getPointsChange();
        System.out.println("Processing points for customer: " + event.getCustomerId());
        System.out.println("Transaction amount: " + event.getAmount());
        rewardsAccount.setPointsBalance(rewardsAccount.getPointsBalance() + points);

    }
}
