package com.example.merchantproviderservice.dto;

import com.example.merchantproviderservice.fulfillment.FulfillmentScenario;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GiftCardFulfillmentRequest {
    @NotNull(message = "Redemption ID is required")
    @Positive(message = "Redemption ID must be greater than zero")
    private Long redemptionId;

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be greater than zero")
    private Long customerId;

    @NotEmpty(message = "Reward IDs must not be empty")
    private List<
            @Positive(message = "Reward ID must be greater than zero")
                    Long> rewardIds;
}
