package com.example.redemptionservice.outbox;

import com.example.redemptionservice.event.RedemptionCompletedEvent;
import com.example.redemptionservice.event.RedemptionEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedemptionOutboxPublisher {
    private final RedemptionOutboxEventRepository redemptionOutboxEventRepository;
    private final ObjectMapper objectMapper;
    private final RedemptionEventProducer redemptionEventProducer;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents(){
        var events = redemptionOutboxEventRepository.
                findByPublishedFalseOrderByCreatedAtAsc();

        for (var outboxEvent : events) {
            try {
                var event = objectMapper.readValue(
                        outboxEvent.getPayload(),
                        RedemptionCompletedEvent.class
                );

                redemptionEventProducer.publishRedemptionCompleted(event);

                outboxEvent.setPublished(true);
                outboxEvent.setPublishedAt(Instant.now());

                redemptionOutboxEventRepository.save(outboxEvent);

            } catch (Exception e) {
                log.error(
                        "Failed to publish redemption outbox event {}",
                        outboxEvent.getEventId(),
                        e
                );
            }
        }

    }
}
