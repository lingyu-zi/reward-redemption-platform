package com.example.redemptionservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedemptionCompensatedProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishRedemptionCompensated(RedemptionCompensatedEvent event) {
        kafkaTemplate.send("redemption-compensated",
                event.getRedemptionId().toString(),
                event).join();
    }
}
