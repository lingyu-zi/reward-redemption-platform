package com.example.redemptionservice.event;

import com.example.redemptionservice.compensation.RedemptionCompensationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedemptionCompensationConsumer {
    private final RedemptionCompensationService redemptionCompensationService;

    @KafkaListener(
            topics = "redemption-failed",
            groupId = "redemption-compensation"
    )
    public void consume(RedemptionFailedEvent event) {

        System.out.println(
                "Received RedemptionFailedEvent: redemptionId="
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
