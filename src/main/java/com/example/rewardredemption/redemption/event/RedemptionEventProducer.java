package com.example.rewardredemption.redemption.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedemptionEventProducer {
    private final KafkaTemplate<String, RedemptionCompletedEvent> kafkaTemplate;

    public void publishRedemptionCompleted(RedemptionCompletedEvent event){
        kafkaTemplate.send("redemption-completed", event.getCustomerId().toString(), event);
    }
}
