package com.example.fulfillmentservice.event;

import com.example.fulfillmentservice.fulfillment.FulfillmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedemptionRequestedConsumer {
    private final FulfillmentService fulfillmentService;

    @KafkaListener(
            topics = "redemption-requested",
            groupId = "fulfillment-service"
    )
    public void consume(RedemptionRequestedEvent event) {
        fulfillmentService.fulfill(event);
    }
}
