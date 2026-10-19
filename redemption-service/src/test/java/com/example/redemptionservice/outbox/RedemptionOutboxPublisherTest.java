package com.example.redemptionservice.outbox;

import com.example.redemptionservice.event.RedemptionCompletedEvent;
import com.example.redemptionservice.event.RedemptionEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedemptionOutboxPublisherTest {
    @Mock
    private RedemptionOutboxEventRepository redemptionOutboxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedemptionEventProducer redemptionEventProducer;

    @InjectMocks
    private RedemptionOutboxPublisher redemptionOutboxPublisher;

    @Test
    void shouldPublishPendingEventAndMarkAsPublished(){
        var redemptionOutboxEvent = createRedemptionOutboxEvent();

        var redemptionEvent = createRedemptionEvent(redemptionOutboxEvent);

        when(redemptionOutboxEventRepository
                .findByPublishedFalseOrderByCreatedAtAsc())
                .thenReturn(List.of(redemptionOutboxEvent));

        when(objectMapper.readValue(
                redemptionOutboxEvent.getPayload(),
                RedemptionCompletedEvent.class))
                .thenReturn(redemptionEvent);

        assertFalse(redemptionOutboxEvent.isPublished());

        redemptionOutboxPublisher.publishPendingEvents();

        assertTrue(redemptionOutboxEvent.isPublished());
        assertNotNull(redemptionOutboxEvent.getPublishedAt());

        verify(redemptionEventProducer).publishRedemptionCompleted(redemptionEvent);
        verify(redemptionOutboxEventRepository).save(redemptionOutboxEvent);
    }

    @Test
    void shouldKeepEventUnpublishedWhenPublishFails(){
        var redemptionOutboxEvent = createRedemptionOutboxEvent();

        var redemptionEvent = createRedemptionEvent(redemptionOutboxEvent);

        when(redemptionOutboxEventRepository
                .findByPublishedFalseOrderByCreatedAtAsc())
                .thenReturn(List.of(redemptionOutboxEvent));

        when(objectMapper.readValue(
                redemptionOutboxEvent.getPayload(),
                RedemptionCompletedEvent.class))
                .thenReturn(redemptionEvent);

        doThrow(new RuntimeException("Kafka unavailable"))
                .when(redemptionEventProducer)
                .publishRedemptionCompleted(redemptionEvent);

        redemptionOutboxPublisher.publishPendingEvents();

        assertFalse(redemptionOutboxEvent.isPublished());
        assertNull(redemptionOutboxEvent.getPublishedAt());

        verify(redemptionEventProducer)
                .publishRedemptionCompleted(redemptionEvent);

        verify(redemptionOutboxEventRepository, never())
                .save(redemptionOutboxEvent);
    }

    @Test
    void shouldDoNothingWhenNoPendingEvents(){
        when(redemptionOutboxEventRepository
                .findByPublishedFalseOrderByCreatedAtAsc())
                .thenReturn(List.of());

        redemptionOutboxPublisher.publishPendingEvents();

        verifyNoInteractions(redemptionEventProducer);
        verifyNoInteractions(objectMapper);

        verify(redemptionOutboxEventRepository, never())
                .save(any());
    }

    private RedemptionOutboxEvent createRedemptionOutboxEvent() {
        var redemptionOutboxEvent = new RedemptionOutboxEvent();
        redemptionOutboxEvent.setId(1L);
        redemptionOutboxEvent.setEventId(UUID.randomUUID());
        redemptionOutboxEvent.setEventType("REDEMPTION_COMPLETED");
        redemptionOutboxEvent.setPayload("{\"test\":\"payload\"}");
        redemptionOutboxEvent.setPublished(false);
        return redemptionOutboxEvent;
    }

    private RedemptionCompletedEvent createRedemptionEvent(RedemptionOutboxEvent redemptionOutboxEvent) {
        var redemptionEvent = new RedemptionCompletedEvent();
        redemptionEvent.setRedemptionId(1L);
        redemptionEvent.setEventId(redemptionOutboxEvent.getEventId());
        return redemptionEvent;
    }
}