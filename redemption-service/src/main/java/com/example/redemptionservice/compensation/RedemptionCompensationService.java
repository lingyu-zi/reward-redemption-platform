package com.example.redemptionservice.compensation;

import com.example.redemptionservice.account.RewardsAccountRepository;
import com.example.redemptionservice.event.RedemptionCompensatedEvent;
import com.example.redemptionservice.exception.BadRequestException;
import com.example.redemptionservice.exception.ResourceNotFoundException;
import com.example.redemptionservice.outbox.RedemptionOutboxEvent;
import com.example.redemptionservice.outbox.RedemptionOutboxEventRepository;
import com.example.redemptionservice.redemption.RedemptionRepository;
import com.example.redemptionservice.redemption.RedemptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RedemptionCompensationService {
    private final RedemptionCompensationRepository redemptionCompensationRepository;
    private final RedemptionRepository redemptionRepository;
    private final RewardsAccountRepository rewardsAccountRepository;
    private final RedemptionOutboxEventRepository redemptionOutboxEventRepository;
    private final ObjectMapper objectMapper;


    @Transactional
    public void compensate(UUID eventId, Long redemptionId,
                           Long customerId, Long pointsToReturn) {
        // idempotency checks
        if (redemptionCompensationRepository.existsByEventId(eventId)) {
            return;
        }
        if (redemptionCompensationRepository.existsByRedemptionId(redemptionId)) {
            return;
        }

        if (pointsToReturn == null || pointsToReturn <= 0) {
            throw new BadRequestException(
                    "Points to return must be greater than zero"
            );
        }

        var redemption = redemptionRepository.findById(redemptionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Redemption not found with id: " + redemptionId
                ));

        if (redemption.getStatus() == RedemptionStatus.COMPENSATED) {
            return;
        }

        if (!redemption.getCustomerId().equals(customerId)) {
            throw new BadRequestException(
                    "Redemption does not belong to customer: " + customerId
            );
        }

        var rewardsAccount = rewardsAccountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rewards account not found for customer id: " + customerId
                ));


        rewardsAccount.setPointsBalance(
                rewardsAccount.getPointsBalance() + pointsToReturn);

        redemption.setStatus(RedemptionStatus.COMPENSATED);

        var compensation = new RedemptionCompensation();
        compensation.setEventId(eventId);
        compensation.setRedemptionId(redemptionId);
        compensation.setCustomerId(customerId);
        compensation.setStatus(CompensationStatus.COMPLETED);
        compensation.setPointsReturned(pointsToReturn);

        redemptionCompensationRepository.save(compensation);

        var compensatedEvent = new RedemptionCompensatedEvent();
        compensatedEvent.setEventId(UUID.randomUUID());
        compensatedEvent.setOccurredAt(Instant.now());
        compensatedEvent.setRedemptionId(redemptionId);
        compensatedEvent.setCustomerId(customerId);
        compensatedEvent.setPointsReturned(pointsToReturn);
        compensatedEvent.setRemainingPointsBalance(rewardsAccount.getPointsBalance());

        var payload = objectMapper.writeValueAsString(compensatedEvent);

        var outboxEvent = new RedemptionOutboxEvent();

        outboxEvent.setEventId(compensatedEvent.getEventId());
        outboxEvent.setEventType("REDEMPTION_COMPENSATED");
        outboxEvent.setPayload(payload);

        redemptionOutboxEventRepository.save(outboxEvent);
    }
}
