package com.example.pointsprocessingservice;

import com.example.pointsprocessingservice.account.RewardsAccount;
import com.example.pointsprocessingservice.account.RewardsAccountRepository;
import com.example.pointsprocessingservice.event.TransactionCompletedEvent;
import com.example.pointsprocessingservice.exception.ResourceNotFoundException;
import com.example.pointsprocessingservice.idempotency.ProcessedEvent;
import com.example.pointsprocessingservice.idempotency.ProcessedEventRepository;
import com.example.pointsprocessingservice.service.PointsProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointsProcessingServiceTest {
    @Mock
    private RewardsAccountRepository rewardsAccountRepository;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private PointsProcessingService pointsProcessingService;

    @Test
    void shouldAddPointsWhenEventIsNew() {
        // Arrange
        var eventId = UUID.randomUUID();
        var customerId = 1L;
        var initialBalance = 1000L;
        var pointsChange = 500L;
        var expectedBalance = 1500L;
        var consumerName = "points-processing";

        var event = new TransactionCompletedEvent();
        event.setEventId(eventId);
        event.setCustomerId(customerId);
        event.setPointsChange(pointsChange);

        var rewardsAccount = new RewardsAccount();
        rewardsAccount.setCustomerId(customerId);
        rewardsAccount.setPointsBalance(initialBalance);

        when(processedEventRepository.existsByEventIdAndConsumerName(
                eventId, consumerName))
                .thenReturn(false);

        when(rewardsAccountRepository.findByCustomerId(customerId))
                .thenReturn(Optional.of(rewardsAccount));

        // Act
        pointsProcessingService.processTransaction(event);

        // Assert
        assertEquals(expectedBalance, rewardsAccount.getPointsBalance());

        verify(processedEventRepository).save(any(ProcessedEvent.class));
    }

    @Test
    void shouldNotAddPointsWhenEventIsDuplicate(){
        // Arrange
        var eventId = UUID.randomUUID();
        var customerId = 1L;
        var initialBalance = 1000L;
        var pointsChange = 500L;
        var expectedBalance = 1000L;
        var consumerName = "points-processing";

        var event = new TransactionCompletedEvent();
        event.setEventId(eventId);
        event.setCustomerId(customerId);
        event.setPointsChange(pointsChange);

        var rewardsAccount = new RewardsAccount();
        rewardsAccount.setCustomerId(customerId);
        rewardsAccount.setPointsBalance(initialBalance);

        when(processedEventRepository.existsByEventIdAndConsumerName(
                eventId, consumerName)).thenReturn(true);

        // Act
        pointsProcessingService.processTransaction(event);

        // Assert
        assertEquals(expectedBalance, rewardsAccount.getPointsBalance());

        verifyNoInteractions(rewardsAccountRepository);

        verify(processedEventRepository, never()).save(any(ProcessedEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenRewardsAccountNotFound(){
        var eventId = UUID.randomUUID();
        var customerId = 1L;
        var pointsChange = 500L;
        var consumerName = "points-processing";

        var event = new TransactionCompletedEvent();
        event.setEventId(eventId);
        event.setCustomerId(customerId);

        var rewardsAccount = new RewardsAccount();
        rewardsAccount.setCustomerId(customerId);
        rewardsAccount.setPointsBalance(pointsChange);

        when(processedEventRepository.existsByEventIdAndConsumerName(
                eventId, consumerName))
                .thenReturn(false);

        when(rewardsAccountRepository.findByCustomerId(customerId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> pointsProcessingService.processTransaction(event));

        verify(processedEventRepository, never()).save(any(ProcessedEvent.class));

    }
}
