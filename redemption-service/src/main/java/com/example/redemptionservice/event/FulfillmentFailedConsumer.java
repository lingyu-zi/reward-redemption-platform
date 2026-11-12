package com.example.redemptionservice.event;

import com.example.redemptionservice.compensation.RedemptionCompensationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FulfillmentFailedConsumer {
    private final RedemptionCompensationService redemptionCompensationService;

    @KafkaListener(
            topics = "fulfillment-failed",
            groupId = "redemption-compensation",
            properties = {
                    "spring.json.value.default.type=com.example.redemptionservice.event.FulfillmentFailedEvent"
            }
    )
    public void consume(FulfillmentFailedEvent event) {

        System.out.println(
                "Received FulfillmentFailedEvent: redemptionId="
                        + event.getRedemptionId()
                        + ", pointsToReturn="
                        + event.getPointsToReturn()
        );

        redemptionCompensationService.compensate(
                event.getEventId(),
                event.getRedemptionId(),
                event.getCustomerId(),
                event.getPointsToReturn()
        );
    }
}
