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

    public void recordRedemptionCompleted(Long customerId,
                                          Long redemptionId, List<Long> rewardIds, Long totalPointsCost) {
        CustomerActivityKey key = new CustomerActivityKey();
        key.setCustomerId(customerId);
        key.setEventTime(Instant.now());
        key.setActivityId(Uuids.timeBased());
        CustomerActivity customerActivity = new CustomerActivity();
        customerActivity.setKey(key);
        customerActivity.setActivityType(ActivityType.REDEMPTION_COMPLETED.name());
        customerActivity.setRedemptionId(redemptionId);
        customerActivity.setRewardIds(rewardIds);
        customerActivity.setTotalPointsCost(totalPointsCost);
        customerActivityRepository.save(customerActivity);
    }

    public List<CustomerActivityResponse> getCustomerActivities(Long customerId) {
        return customerActivityRepository.findByKeyCustomerId(customerId).stream()
                .map(customerActivityMapper::toCustomerActivityResponse)
                .toList();
    }
}
