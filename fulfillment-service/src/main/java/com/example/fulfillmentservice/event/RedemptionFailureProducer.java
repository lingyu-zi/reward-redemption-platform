package com.example.fulfillmentservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedemptionFailureProducer {
    private final KafkaTemplate<String, RedemptionFailedEvent> kafkaTemplate;

    public void publish(RedemptionFailedEvent event) {

        System.out.println(
                "Publishing RedemptionFailedEvent: redemptionId="
                        + event.getRedemptionId()
                        + ", pointsToReturn="
                        + event.getPointsToReturn()
        );

        kafkaTemplate.send(
                "redemption-failed",
                event.getRedemptionId().toString(),
                event
        ).join();
    }
}
