package com.example.rewardredemption.activity.event;

import com.example.rewardredemption.activity.CustomerActivityService;
import com.example.rewardredemption.transaction.event.TransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TransactionActivityConsumer {
    private final CustomerActivityService customerActivityService;

    @KafkaListener(
            topics = "transaction-completed",
            groupId = "activity-history-group"
    )
    public void earnPointsActivity(TransactionCompletedEvent event) {
        customerActivityService.recordPointsEarned(
                event.getCustomerId(),
                event.getTransactionId(),
                event.getPointsChange());

    }
}
