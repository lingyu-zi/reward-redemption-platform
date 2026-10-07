package com.example.rewardredemption.transaction.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEventProducer {
    private final KafkaTemplate<String, TransactionCompletedEvent> kafkaTemplate;

    public void publishTransactionCompleted(TransactionCompletedEvent event){
        kafkaTemplate.send("transaction-completed", event.getCustomerId().toString(), event);
    }
}
