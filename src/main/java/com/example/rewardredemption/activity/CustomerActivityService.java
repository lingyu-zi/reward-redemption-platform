package com.example.rewardredemption.activity;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.example.rewardredemption.activity.dto.CustomerActivityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerActivityService {
    private final CustomerActivityRepository customerActivityRepository;
    private final CustomerActivityMapper customerActivityMapper;

    public void recordRedemptionCompleted(Long customerId, Long redemptionId,
                                          List<Long> rewardIds, Long pointsChange) {
        var key = new CustomerActivityKey();
        key.setCustomerId(customerId);
        key.setEventTime(Instant.now());
        key.setActivityId(Uuids.timeBased());

        var customerActivity = new CustomerActivity();
        customerActivity.setKey(key);
        customerActivity.setActivityType(ActivityType.POINTS_REDEEMED.name());
        customerActivity.setRedemptionId(redemptionId);
        customerActivity.setRewardIds(rewardIds);
        customerActivity.setPointsChange(pointsChange);
        customerActivityRepository.save(customerActivity);
    }

    public void recordPointsEarned(Long customerId, Long transactionId, Long pointsEarned) {
        var key = new CustomerActivityKey();
        key.setCustomerId(customerId);
        key.setEventTime(Instant.now());
        key.setActivityId(Uuids.timeBased());
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
