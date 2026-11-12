package com.example.fulfillmentservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FulfillmentFailedProducer {
    private final KafkaTemplate<String, FulfillmentFailedEvent> kafkaTemplate;

    public void publishFulfillmentFailed(FulfillmentFailedEvent event) {

        System.out.println(
                "Publishing FulfillmentFailedEvent: redemptionId="
                        + event.getRedemptionId()
                        + ", pointsToReturn="
                        + event.getPointsToReturn()
        );

        kafkaTemplate.send(
                "fulfillment-failed",
                event.getRedemptionId().toString(),
                event
        ).join();
    }
}
