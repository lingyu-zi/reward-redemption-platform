package com.example.rewardredemption.activity.event;

import com.example.rewardredemption.activity.CustomerActivityService;
import com.example.rewardredemption.redemption.event.RedemptionCompletedEvent;
import com.example.rewardredemption.transaction.event.TransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ActivityHistoryConsumer {
    private final CustomerActivityService customerActivityService;

    // Transaction completed
    @KafkaListener(
            topics = "transaction-completed",
            groupId = "activity-history-group"
    )
    public void transactionCompletedActivity(TransactionCompletedEvent event) {
        customerActivityService.recordPointsEarned(
                event.getCustomerId(),
                event.getTransactionId(),
                event.getPointsChange());

    }

    // Redemption completed
    @KafkaListener(
            topics = "redemption-completed",
            groupId = "activity-history-group"
    )
    public void saveRedemptionActivity(RedemptionCompletedEvent event) {
        customerActivityService.recordRedemptionCompleted(
                event.getCustomerId(),
                event.getRedemptionId(),
                event.getRewardIds(),
                event.getPointsChange()
        );
    }
}
