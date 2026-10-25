package com.example.fulfillmentservice.event;

import com.example.fulfillmentservice.fulfillment.FulfillmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FulfillmentConsumer {
    private final FulfillmentService fulfillmentService;

    @KafkaListener(
            topics = "redemption-completed",
            groupId = "fulfillment-service"
    )
    public void consume(RedemptionCompletedEvent event) {
        fulfillmentService.fulfill(event);
    }
}
