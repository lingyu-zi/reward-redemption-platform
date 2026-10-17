package com.example.pointsprocessingservice.service;


import com.example.pointsprocessingservice.account.RewardsAccountRepository;
import com.example.pointsprocessingservice.event.TransactionCompletedEvent;
import com.example.pointsprocessingservice.exception.ResourceNotFoundException;
import com.example.pointsprocessingservice.idempotency.ProcessedEvent;
import com.example.pointsprocessingservice.idempotency.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PointsProcessingService {
    private final RewardsAccountRepository rewardsAccountRepository;
    private final ProcessedEventRepository processedEventRepository;
    private static final String CONSUMER_NAME = "points-processing";

    @Transactional
    public void processTransaction(TransactionCompletedEvent event) {
        // check duplicate
        if (processedEventRepository.existsByEventIdAndConsumerName(
                event.getEventId(), CONSUMER_NAME)) {
            return ;
        }

        // add points
        var customerId = event.getCustomerId();
        var rewardsAccount= rewardsAccountRepository.findByCustomerId(customerId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Rewards account not found for customer id: " + customerId));
        var points = event.getPointsChange();
        rewardsAccount.setPointsBalance(rewardsAccount.getPointsBalance() + points);

        // save processed event
        var processedEvent = new ProcessedEvent();
        processedEvent.setEventId(event.getEventId());
        processedEvent.setConsumerName(CONSUMER_NAME);
        processedEventRepository.save(processedEvent);
    }
}
