package com.example.redemptionservice.redemption.dto;

import com.example.redemptionservice.redemption.RedemptionStatus;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class RedemptionResponse {
    private Long id;
    private Long customerId;
    private Long totalPoints;
    private RedemptionStatus status;
    private Instant createdAt;
    private List<RedemptionItemResponse> items;
}
