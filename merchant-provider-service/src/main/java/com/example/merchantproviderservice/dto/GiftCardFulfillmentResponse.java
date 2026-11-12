package com.example.merchantproviderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GiftCardFulfillmentResponse {
    private Long redemptionId;
    private String status;
    private String confirmationCode;
}
