package com.example.activityhistoryservice.activity.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class CustomerActivityResponse {
    private String activityType;
    private Instant eventTime;
    private Long pointsChange;
    private List<Long> rewardIds;
    private Long redemptionId;
    private Long transactionId;
}
