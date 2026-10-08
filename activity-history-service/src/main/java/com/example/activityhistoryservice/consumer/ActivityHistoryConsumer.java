package com.example.activityhistoryservice.consumer;


import com.example.activityhistoryservice.activity.CustomerActivityService;
import com.example.activityhistoryservice.event.RedemptionCompletedEvent;
import com.example.activityhistoryservice.event.TransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class ActivityHistoryConsumer {
    private final ObjectMapper objectMapper;
    private final CustomerActivityService customerActivityService;

    // Transaction completed
    @KafkaListener(
            topics = "transaction-completed",
            groupId = "activity-history-service-test-group"
    )
    public void saveTransactionActivity(String message) {
        var event = objectMapper.readValue(
                message, TransactionCompletedEvent.class);
        log.info(
                "[activity-history-service] received transaction event {}",
                event.getTransactionId()
        );
        customerActivityService.recordPointsEarned(
                event.getCustomerId(),
                event.getTransactionId(),
                event.getPointsChange());

    }

    // Redemption completed
    @KafkaListener(
            topics = "redemption-completed",
            groupId = "activity-history-service-test-group"
    )
    public void saveRedemptionActivity(String message) {
        var event = objectMapper.readValue(
                message, RedemptionCompletedEvent.class);
        log.info(
                "[activity-history-service] received redemption event {}",
                event.getRedemptionId()
        );
        customerActivityService.recordRedemptionCompleted(
                event.getCustomerId(),
                event.getRedemptionId(),
                event.getRewardIds(),
                event.getPointsChange()
        );
    }
}
