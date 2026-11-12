package com.example.fulfillmentservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FulfillmentSucceededProducer {
    private final KafkaTemplate<String, FulfillmentSucceededEvent> kafkaTemplate;

    public void publishFulfillmentSucceeded(FulfillmentSucceededEvent event) {
        kafkaTemplate.send(
                "fulfillment-succeeded",
                event.getRedemptionId().toString(),
                event
        );
    }
}
