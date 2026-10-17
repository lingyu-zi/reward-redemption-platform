package com.example.activityhistoryservice.consumer;


import com.example.activityhistoryservice.activity.CustomerActivityService;
import com.example.activityhistoryservice.event.RedemptionCompletedEvent;
import com.example.activityhistoryservice.event.TransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Component
public class ActivityHistoryConsumer {
    private final ObjectMapper objectMapper;
    private final CustomerActivityService customerActivityService;

    // Transaction completed
    @KafkaListener(
            topics = "transaction-completed",
            groupId = "activity-history-service"
    )
    public void saveTransactionActivity(String message) {
        var event = objectMapper.readValue(
                message, TransactionCompletedEvent.class);
        customerActivityService.recordPointsEarned(
                event.getEventId(),
                event.getOccurredAt(),
                event.getCustomerId(),
                event.getTransactionId(),
                event.getPointsChange());

    }

    // Redemption completed
    @KafkaListener(
            topics = "redemption-completed",
            groupId = "activity-history-service"
    )
    public void saveRedemptionActivity(String message) {
        var event = objectMapper.readValue(
                message, RedemptionCompletedEvent.class);
        customerActivityService.recordRedemptionCompleted(
                event.getEventId(),
                event.getOccurredAt(),
                event.getCustomerId(),
                event.getRedemptionId(),
                event.getRewardIds(),
                event.getPointsChange()
        );
    }
}
