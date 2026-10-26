package com.example.redemptionservice.compensation;

import com.example.redemptionservice.account.RewardsAccount;
import com.example.redemptionservice.account.RewardsAccountRepository;
import com.example.redemptionservice.exception.BadRequestException;
import com.example.redemptionservice.exception.ResourceNotFoundException;
import com.example.redemptionservice.redemption.Redemption;
import com.example.redemptionservice.redemption.RedemptionRepository;
import com.example.redemptionservice.redemption.RedemptionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedemptionCompensationServiceTest {
    @Mock
    private RedemptionCompensationRepository redemptionCompensationRepository;

    @Mock
    private RedemptionRepository redemptionRepository;

    @Mock
    private RewardsAccountRepository rewardsAccountRepository;

    @InjectMocks
    private RedemptionCompensationService redemptionCompensationService;

    @Test
    void shouldReturnPointsAndMarkRedemptionCompensated() {
        var eventId = UUID.randomUUID();
        var redemptionId = 1L;
        var customerId = 10L;
        var pointsToReturn = 100L;
        var balance = 1000L;

        var redemption = new Redemption();
        redemption.setId(redemptionId);
        redemption.setCustomerId(customerId);
        redemption.setStatus(RedemptionStatus.COMPLETED);

        var rewardsAccount = new RewardsAccount();
        rewardsAccount.setCustomerId(customerId);
        rewardsAccount.setPointsBalance(balance);

        when(redemptionCompensationRepository
                .existsByEventId(eventId)).thenReturn(false);

        when(redemptionCompensationRepository
                .existsByRedemptionId(redemptionId)).thenReturn(false);

        when(redemptionRepository.findById(redemptionId))
                .thenReturn(Optional.of(redemption));

        when(rewardsAccountRepository.findByCustomerId(customerId))
                .thenReturn(Optional.of(rewardsAccount));

        redemptionCompensationService.compensate(
                eventId, redemptionId, customerId, pointsToReturn);

        assertEquals(balance + pointsToReturn,
                rewardsAccount.getPointsBalance());
        assertEquals(RedemptionStatus.COMPENSATED, redemption.getStatus());

        verify(redemptionCompensationRepository)
                .existsByEventId(eventId);
        verify(redemptionCompensationRepository)
                .existsByRedemptionId(redemptionId);

        verify(redemptionRepository).findById(redemptionId);
        verify(rewardsAccountRepository).findByCustomerId(customerId);

        var captor = ArgumentCaptor.forClass(RedemptionCompensation.class);
        verify(redemptionCompensationRepository).save(captor.capture());
        var savedCompensation = captor.getValue();

        assertEquals(eventId, savedCompensation.getEventId());
        assertEquals(redemptionId, savedCompensation.getRedemptionId());
        assertEquals(customerId, savedCompensation.getCustomerId());
        assertEquals(CompensationStatus.COMPLETED, savedCompensation.getStatus());
        assertEquals(pointsToReturn, savedCompensation.getPointsReturned());
    }

    @Test
    void shouldIgnoreDuplicateEventId() {
        var eventId = UUID.randomUUID();
        var redemptionId = 1L;
        var customerId = 10L;
        var pointsToReturn = 100L;

        when(redemptionCompensationRepository.existsByEventId(eventId))
                .thenReturn(true);

        redemptionCompensationService.compensate(
                eventId, redemptionId, customerId, pointsToReturn);

        verify(redemptionCompensationRepository)
                .existsByEventId(eventId);
        verify(redemptionCompensationRepository, never())
                .existsByRedemptionId(redemptionId);

        verifyNoInteractions(redemptionRepository);
        verifyNoInteractions(rewardsAccountRepository);

        verify(redemptionCompensationRepository, never())
                .save(any(RedemptionCompensation.class));
    }

    @Test
    void shouldIgnoreDuplicateRedemptionId() {
        var eventId = UUID.randomUUID();
        var redemptionId = 1L;
        var customerId = 10L;
        var pointsToReturn = 100L;

        when(redemptionCompensationRepository.existsByEventId(eventId))
                .thenReturn(false);

        when(redemptionCompensationRepository.existsByRedemptionId(redemptionId))
                .thenReturn(true);

        redemptionCompensationService.compensate(
                eventId, redemptionId, customerId, pointsToReturn);

        verify(redemptionCompensationRepository)
                .existsByEventId(eventId);
        verify(redemptionCompensationRepository)
                .existsByRedemptionId(redemptionId);

        verifyNoInteractions(redemptionRepository);
        verifyNoInteractions(rewardsAccountRepository);

        verify(redemptionCompensationRepository, never())
                .save(any(RedemptionCompensation.class));
    }

    @Test
    void shouldRejectInvalidPointsToReturn() {
        var eventId = UUID.randomUUID();
        var redemptionId = 1L;
        var customerId = 10L;
        var pointsToReturn = -100L;

        when(redemptionCompensationRepository.existsByEventId(eventId))
                .thenReturn(false);

        when(redemptionCompensationRepository.existsByRedemptionId(redemptionId))
                .thenReturn(false);

        var exception = assertThrows(BadRequestException.class,
                () -> redemptionCompensationService.compensate(
                        eventId, redemptionId, customerId, pointsToReturn)
        );

        assertEquals(
                "Points to return must be greater than zero",
                exception.getMessage());

        verify(redemptionCompensationRepository)
                .existsByEventId(eventId);
        verify(redemptionCompensationRepository)
                .existsByRedemptionId(redemptionId);

        verifyNoInteractions(redemptionRepository);
        verifyNoInteractions(rewardsAccountRepository);

        verify(redemptionCompensationRepository, never())
                .save(any(RedemptionCompensation.class));
    }

    @Test
    void shouldRejectNullPointsToReturn() {
        var eventId = UUID.randomUUID();
        var redemptionId = 1L;
        var customerId = 10L;
        Long pointsToReturn = null;

        when(redemptionCompensationRepository.existsByEventId(eventId))
                .thenReturn(false);

        when(redemptionCompensationRepository.existsByRedemptionId(redemptionId))
                .thenReturn(false);

        var exception = assertThrows(BadRequestException.class,
                () -> redemptionCompensationService.compensate(
                        eventId, redemptionId, customerId, pointsToReturn)
        );

        assertEquals(
                "Points to return must be greater than zero",
                exception.getMessage());

        verify(redemptionCompensationRepository)
                .existsByEventId(eventId);
        verify(redemptionCompensationRepository)
                .existsByRedemptionId(redemptionId);

        verifyNoInteractions(redemptionRepository);
        verifyNoInteractions(rewardsAccountRepository);

        verify(redemptionCompensationRepository, never())
                .save(any(RedemptionCompensation.class));
    }

    @Test
    void shouldIgnoreAlreadyCompensatedRedemption() {
        var eventId = UUID.randomUUID();
        var redemptionId = 1L;
        var customerId = 10L;
        var pointsToReturn = 100L;

        var redemption = new Redemption();
        redemption.setId(redemptionId);
        redemption.setCustomerId(customerId);
        redemption.setStatus(RedemptionStatus.COMPENSATED);

        when(redemptionCompensationRepository.existsByEventId(eventId))
                .thenReturn(false);

        when(redemptionCompensationRepository.existsByRedemptionId(redemptionId))
                .thenReturn(false);

        when(redemptionRepository.findById(redemptionId))
                .thenReturn(Optional.of(redemption));

        redemptionCompensationService.compensate(
                eventId, redemptionId, customerId, pointsToReturn);

        verify(redemptionCompensationRepository)
                .existsByEventId(eventId);

        verify(redemptionCompensationRepository)
                .existsByRedemptionId(redemptionId);

        verify(redemptionRepository).findById(redemptionId);

        verifyNoInteractions(rewardsAccountRepository);

        verify(redemptionCompensationRepository, never())
                .save(any(RedemptionCompensation.class));
    }

    @Test
    void shouldRejectCustomerMismatch() {
        var eventId = UUID.randomUUID();
        var redemptionId = 1L;
        var customerId = 10L;
        var pointsToReturn = 100L;

        var redemption = new Redemption();
        redemption.setId(redemptionId);
        redemption.setCustomerId(2L);
        redemption.setStatus(RedemptionStatus.COMPLETED);

        when(redemptionCompensationRepository.existsByEventId(eventId))
                .thenReturn(false);

        when(redemptionCompensationRepository.existsByRedemptionId(redemptionId))
                .thenReturn(false);

        when(redemptionRepository.findById(redemptionId))
                .thenReturn(Optional.of(redemption));

        var exception = assertThrows(BadRequestException.class,
                () -> redemptionCompensationService.compensate(
                        eventId, redemptionId, customerId, pointsToReturn)
        );

        assertEquals(
                "Redemption does not belong to customer: " + customerId,
                exception.getMessage());

        verify(redemptionCompensationRepository)
                .existsByEventId(eventId);

        verify(redemptionCompensationRepository)
                .existsByRedemptionId(redemptionId);

        verify(redemptionRepository).findById(redemptionId);

        verifyNoInteractions(rewardsAccountRepository);

        verify(redemptionCompensationRepository, never())
                .save(any(RedemptionCompensation.class));
    }

    @Test
    void shouldThrowWhenRedemptionNotFound() {
        var eventId = UUID.randomUUID();
        var redemptionId = 1L;
        var customerId = 10L;
        var pointsToReturn = 100L;

        when(redemptionCompensationRepository.existsByEventId(eventId))
                .thenReturn(false);

        when(redemptionCompensationRepository.existsByRedemptionId(redemptionId))
                .thenReturn(false);

        when(redemptionRepository.findById(redemptionId))
                .thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> redemptionCompensationService.compensate(
                        eventId, redemptionId, customerId, pointsToReturn)
        );

        assertEquals(
                "Redemption not found with id: " + redemptionId,
                exception.getMessage());

        verify(redemptionCompensationRepository)
                .existsByEventId(eventId);

        verify(redemptionCompensationRepository)
                .existsByRedemptionId(redemptionId);

        verify(redemptionRepository).findById(redemptionId);

        verifyNoInteractions(rewardsAccountRepository);

        verify(redemptionCompensationRepository, never())
                .save(any(RedemptionCompensation.class));
    }

    @Test
    void shouldThrowWhenRewardsAccountNotFound() {
        var eventId = UUID.randomUUID();
        var redemptionId = 1L;
        var customerId = 10L;
        var pointsToReturn = 100L;

        var redemption = new Redemption();
        redemption.setId(redemptionId);
        redemption.setCustomerId(customerId);
        redemption.setStatus(RedemptionStatus.COMPLETED);

        when(redemptionCompensationRepository.existsByEventId(eventId))
                .thenReturn(false);

        when(redemptionCompensationRepository.existsByRedemptionId(redemptionId))
                .thenReturn(false);

        when(redemptionRepository.findById(redemptionId))
                .thenReturn(Optional.of(redemption));

        when(rewardsAccountRepository.findByCustomerId(customerId))
                .thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> redemptionCompensationService.compensate(
                        eventId, redemptionId, customerId, pointsToReturn)
        );

        assertEquals(
                "Rewards account not found for customer id: " + customerId,
                exception.getMessage());

        verify(redemptionCompensationRepository)
                .existsByEventId(eventId);

        verify(redemptionCompensationRepository)
                .existsByRedemptionId(redemptionId);

        verify(redemptionRepository).findById(redemptionId);

        verify(rewardsAccountRepository).findByCustomerId(customerId);

        verify(redemptionCompensationRepository, never())
                .save(any(RedemptionCompensation.class));
    }
}