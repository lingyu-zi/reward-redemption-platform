package com.example.redemptionservice.compensation;

import com.example.redemptionservice.account.RewardsAccountRepository;
import com.example.redemptionservice.exception.BadRequestException;
import com.example.redemptionservice.exception.ResourceNotFoundException;
import com.example.redemptionservice.redemption.RedemptionRepository;
import com.example.redemptionservice.redemption.RedemptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RedemptionCompensationService {
    private final RedemptionCompensationRepository redemptionCompensationRepository;
    private final RedemptionRepository redemptionRepository;
    private final RewardsAccountRepository rewardsAccountRepository;

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
    }
}
