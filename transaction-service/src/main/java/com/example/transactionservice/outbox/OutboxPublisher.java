package com.example.transactionservice.outbox;

import com.example.transactionservice.event.TransactionCompletedEvent;
import com.example.transactionservice.event.TransactionEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@RequiredArgsConstructor
@Component
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final TransactionEventProducer transactionEventProducer;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        var events =
                outboxEventRepository.findByPublishedFalseOrderByCreatedAtAsc();

        for (var outboxEvent : events) {

            try {
                var event = objectMapper.readValue(
                        outboxEvent.getPayload(),
                        TransactionCompletedEvent.class
                );

                transactionEventProducer.publishTransactionCompleted(event);

                outboxEvent.setPublished(true);
                outboxEvent.setPublishedAt(Instant.now());

                outboxEventRepository.save(outboxEvent);

            } catch (Exception e) {
                // keep published = false
                // publisher will retry later
            }
        }
    }
}