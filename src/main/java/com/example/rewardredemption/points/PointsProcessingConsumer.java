package com.example.rewardredemption.points;

import com.example.rewardredemption.transaction.event.TransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PointsProcessingConsumer {
    private final PointsProcessingService pointsProcessingService;

    @KafkaListener(topics = "transaction-completed", groupId = "points-processing-group")
    public void pointProcessing(TransactionCompletedEvent event) {
        pointsProcessingService.processTransaction(event);
    }
}
