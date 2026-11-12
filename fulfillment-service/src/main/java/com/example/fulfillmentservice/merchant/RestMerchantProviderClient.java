package com.example.fulfillmentservice.merchant;

import com.example.fulfillmentservice.event.RedemptionRequestedEvent;
import com.example.fulfillmentservice.merchant.dto.GiftCardFulfillmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class RestMerchantProviderClient implements MerchantProviderClient {
    private final RestClient merchantProviderRestClient;

    @Override
    public void fulfill(RedemptionRequestedEvent event) {

        var request = new GiftCardFulfillmentRequest();
        request.setCustomerId(event.getCustomerId());
        request.setRedemptionId(event.getRedemptionId());
        request.setRewardIds(event.getRewardIds());

        // Make HTTP Request
        merchantProviderRestClient
                .post()
                .uri("/api/gift-cards/fulfill")
                .header(
                        "Idempotency-Key",
                        "redemption-" + event.getRedemptionId()
                )
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
