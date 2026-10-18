package com.example.transactionservice.transaction;

import com.example.transactionservice.dto.TransactionResponse;
import com.example.transactionservice.event.TransactionCompletedEvent;
import com.example.transactionservice.exception.BadRequestException;
import com.example.transactionservice.exception.ResourceNotFoundException;
import com.example.transactionservice.outbox.OutboxEvent;
import com.example.transactionservice.outbox.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldCompleteTransactionAndCreateOutboxEvent(){
        var transaction = new Transaction();
        transaction.setId(1L);
        transaction.setCustomerId(100L);
        transaction.setMerchantId(200L);
        transaction.setTransactionReference(UUID.randomUUID());
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setStatus(TransactionStatus.PENDING);

        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(transaction));

        when(objectMapper.writeValueAsString(any(TransactionCompletedEvent.class)))
                .thenReturn("{\"event\":\"transaction-completed\"}");

        var response = new TransactionResponse();

        when(transactionMapper.toTransactionResponse(transaction))
                .thenReturn(response);

        var result = transactionService.completeTransaction(1L);

        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
        assertSame(response, result);

        // Outbox
        var captor = ArgumentCaptor.forClass(OutboxEvent.class);

        verify(outboxEventRepository).save(captor.capture());

        var savedOutboxEvent = captor.getValue();

        assertNotNull(savedOutboxEvent.getEventId());
        assertEquals("TRANSACTION_COMPLETED", savedOutboxEvent.getEventType());
        assertEquals(
                "{\"event\":\"transaction-completed\"}",
                savedOutboxEvent.getPayload()
        );

        var eventCaptor =
                ArgumentCaptor.forClass(TransactionCompletedEvent.class);

        verify(objectMapper)
                .writeValueAsString(eventCaptor.capture());

        var event = eventCaptor.getValue();

        assertNotNull(event.getEventId());
        assertNotNull(event.getOccurredAt());

        assertEquals(1L, event.getTransactionId());
        assertEquals(100L, event.getCustomerId());
        assertEquals(200L, event.getMerchantId());
        assertEquals(new BigDecimal("100.00"), event.getAmount());
        assertEquals(150L, event.getPointsChange());

        assertEquals(
                event.getEventId(),
                savedOutboxEvent.getEventId()
        );

        assertFalse(savedOutboxEvent.isPublished());
        assertNull(savedOutboxEvent.getPublishedAt());
    }

    @Test
    void shouldThrowExceptionWhenTransactionAlreadyCompleted() {
        var transaction = new Transaction();
        transaction.setId(1L);
        transaction.setStatus(TransactionStatus.COMPLETED);

        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(transaction));

        var exception = assertThrows(BadRequestException.class,
                () -> transactionService.completeTransaction(1L));

        assertEquals("Transaction is already completed",
                exception.getMessage());

        verifyNoInteractions(outboxEventRepository);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void shouldThrowExceptionWhenTransactionFailed() {
        var transaction = new Transaction();
        transaction.setId(1L);
        transaction.setStatus(TransactionStatus.FAILED);

        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(transaction));

        var exception = assertThrows(BadRequestException.class,
                () -> transactionService.completeTransaction(1L));

        assertEquals("Transaction cannot be completed from status: FAILED",
                exception.getMessage());

        verifyNoInteractions(outboxEventRepository);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void shouldThrowExceptionWhenTransactionNotFound() {
        when(transactionRepository.findById(1L))
                .thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> transactionService.completeTransaction(1L));

        assertEquals("Transaction not found with id: 1",
                exception.getMessage()
        );

        verifyNoInteractions(outboxEventRepository);
        verifyNoInteractions(objectMapper);
    }

}