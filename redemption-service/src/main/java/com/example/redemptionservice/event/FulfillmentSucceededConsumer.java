package com.example.redemptionservice.event;

import com.example.redemptionservice.redemption.RedemptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FulfillmentSucceededConsumer {
    private final RedemptionService redemptionService;

    @KafkaListener(
            topics = "fulfillment-succeeded",
            groupId = "redemption-completion",
            properties = {
                    "spring.json.value.default.type=com.example.redemptionservice.event.FulfillmentSucceededEvent"
            }
    )
    public void consume(FulfillmentSucceededEvent event) {
        redemptionService.completeRedemption(event);

    }
}
