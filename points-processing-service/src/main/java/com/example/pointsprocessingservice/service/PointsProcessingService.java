package com.example.pointsprocessingservice.service;


import com.example.pointsprocessingservice.account.RewardsAccountRepository;
import com.example.pointsprocessingservice.event.TransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        rewardsAccount.setPointsBalance(rewardsAccount.getPointsBalance() + points);
    }
}
