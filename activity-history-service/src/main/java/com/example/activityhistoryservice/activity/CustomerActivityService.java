package com.example.activityhistoryservice.activity;

import com.example.activityhistoryservice.activity.dto.CustomerActivityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CustomerActivityService {
    private final CustomerActivityRepository customerActivityRepository;
    private final CustomerActivityMapper customerActivityMapper;

    public void recordRedemptionCompleted(UUID eventId, Instant occurredAt,
                                          Long customerId, Long redemptionId,
                                          List<Long> rewardIds, Long pointsChange) {
        var key = new CustomerActivityKey();
        key.setCustomerId(customerId);
        key.setEventTime(occurredAt);
        key.setActivityId(eventId);

        var customerActivity = new CustomerActivity();
        customerActivity.setKey(key);
        customerActivity.setActivityType(ActivityType.POINTS_REDEEMED.name());
        customerActivity.setRedemptionId(redemptionId);
        customerActivity.setRewardIds(rewardIds);
        customerActivity.setPointsChange(pointsChange);
        customerActivityRepository.save(customerActivity);
    }

    public void recordPointsEarned(UUID eventId, Instant occurredAt,
                                   Long customerId, Long transactionId, Long pointsEarned) {
        var key = new CustomerActivityKey();
        key.setCustomerId(customerId);
        key.setEventTime(occurredAt);
        key.setActivityId(eventId);

        var customerActivity = new CustomerActivity();
        customerActivity.setKey(key);
        customerActivity.setActivityType(ActivityType.POINTS_EARNED.name());
        customerActivity.setTransactionId(transactionId);
        customerActivity.setPointsChange(pointsEarned);

        customerActivityRepository.save(customerActivity);
    }

    public List<CustomerActivityResponse> getCustomerActivities(Long customerId) {
        return customerActivityRepository.findByKeyCustomerId(customerId).stream()
                .map(customerActivityMapper::toCustomerActivityResponse)
                .toList();
    }
}
