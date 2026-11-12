package com.example.fulfillmentservice.merchant.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GiftCardFulfillmentRequest {
    private Long redemptionId;
    private Long customerId;
    private List<Long> rewardIds;
}
