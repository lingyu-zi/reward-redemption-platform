package com.example.redemptionservice.outbox;

import com.example.redemptionservice.event.*;
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
    private final RedemptionRequestedProducer redemptionRequestedProducer;
    private final RedemptionCompletedProducer redemptionCompletedProducer;
    private final RedemptionCompensatedProducer redemptionCompensatedProducer;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents(){
        var events = redemptionOutboxEventRepository.
                findByPublishedFalseOrderByCreatedAtAsc();

        for (var outboxEvent : events) {
            try {
                switch (outboxEvent.getEventType()) {
                    case "REDEMPTION_REQUESTED" -> {
                        var event = objectMapper.readValue(
                                outboxEvent.getPayload(),
                                RedemptionRequestedEvent.class
                        );

                        redemptionRequestedProducer
                                .publishRedemptionRequested(event);
                    }

                    case "REDEMPTION_COMPLETED" -> {
                        var event = objectMapper.readValue(
                                outboxEvent.getPayload(),
                                RedemptionCompletedEvent.class
                        );

                        redemptionCompletedProducer
                                .publishRedemptionCompleted(event);
                    }

                    case "REDEMPTION_COMPENSATED" -> {
                        var event = objectMapper.readValue(
                                outboxEvent.getPayload(),
                                RedemptionCompensatedEvent.class
                        );

                        redemptionCompensatedProducer
                                .publishRedemptionCompensated(event);
                    }

                    default -> throw new IllegalStateException(
                            "Unknown redemption outbox event type: "
                                    + outboxEvent.getEventType()
                    );
                }

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
