package com.example.redemptionservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedemptionRequestedProducer {
    private final KafkaTemplate<String, RedemptionRequestedEvent> kafkaTemplate;

    public void publishRedemptionRequested(RedemptionRequestedEvent event){
        kafkaTemplate.send("redemption-requested",
                event.getRedemptionId().toString(),
                event).join();
    }
}
