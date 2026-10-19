package com.example.transactionservice.outbox;

import com.example.transactionservice.event.TransactionCompletedEvent;
import com.example.transactionservice.event.TransactionEventProducer;
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
class OutboxPublisherTest {
    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TransactionEventProducer transactionEventProducer;

    @InjectMocks
    private OutboxPublisher outboxPublisher;

    // Happy path

    @Test
    void shouldPublishPendingEventAndMarkAsPublished() {
        var outboxEvent = createOutboxEvent();

        var transactionEvent = createTransactionEvent(outboxEvent);

        when(outboxEventRepository.findByPublishedFalseOrderByCreatedAtAsc())
                .thenReturn(List.of(outboxEvent));

        when(objectMapper.readValue(
                outboxEvent.getPayload(),
                TransactionCompletedEvent.class))
                .thenReturn(transactionEvent);

        assertFalse(outboxEvent.isPublished());

        outboxPublisher.publishPendingEvents();

        assertTrue(outboxEvent.isPublished());
        assertNotNull(outboxEvent.getPublishedAt());

        verify(transactionEventProducer).publishTransactionCompleted(transactionEvent);
        verify(outboxEventRepository).save(outboxEvent);
    }
    // Kafka failure

    @Test
    void shouldKeepEventUnpublishedWhenPublishFails(){

        var outboxEvent = createOutboxEvent();

        var transactionEvent = createTransactionEvent(outboxEvent);

        when(outboxEventRepository.findByPublishedFalseOrderByCreatedAtAsc())
                .thenReturn(List.of(outboxEvent));

        when(objectMapper.readValue(
                outboxEvent.getPayload(),
                TransactionCompletedEvent.class))
                .thenReturn(transactionEvent);

        doThrow(new RuntimeException("Kafka unavailable"))
                .when(transactionEventProducer)
                .publishTransactionCompleted(transactionEvent);

        outboxPublisher.publishPendingEvents();

        assertFalse(outboxEvent.isPublished());
        assertNull(outboxEvent.getPublishedAt());

        verify(transactionEventProducer)
                .publishTransactionCompleted(transactionEvent);

        verify(outboxEventRepository, never())
                .save(outboxEvent);
    }
    // empty queue path

    @Test
    void shouldDoNothingWhenNoPendingEvents(){

        when(outboxEventRepository
                .findByPublishedFalseOrderByCreatedAtAsc())
                .thenReturn(List.of());

        outboxPublisher.publishPendingEvents();

        verifyNoInteractions(transactionEventProducer);
        verifyNoInteractions(objectMapper);

        verify(outboxEventRepository, never())
                .save(any());
    }

    private OutboxEvent createOutboxEvent() {
        var outboxEvent = new OutboxEvent();
        outboxEvent.setId(1L);
        outboxEvent.setEventId(UUID.randomUUID());
        outboxEvent.setEventType("TRANSACTION_COMPLETED");
        outboxEvent.setPayload("{\"test\":\"payload\"}");
        outboxEvent.setPublished(false);

        return outboxEvent;
    }

    private TransactionCompletedEvent createTransactionEvent(OutboxEvent outboxEvent) {
        var transactionEvent = new TransactionCompletedEvent();
        transactionEvent.setEventId(outboxEvent.getEventId());
        transactionEvent.setCustomerId(1L);

        return transactionEvent;
    }
}