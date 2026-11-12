package com.example.redemptionservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedemptionCompletedProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishRedemptionCompleted(RedemptionCompletedEvent event){
        kafkaTemplate.send("redemption-completed",
                event.getRedemptionId().toString(),
                event).join();
    }
}
