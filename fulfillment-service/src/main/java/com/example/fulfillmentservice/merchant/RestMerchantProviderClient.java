package com.example.fulfillmentservice.merchant;

import com.example.fulfillmentservice.event.RedemptionRequestedEvent;
import com.example.fulfillmentservice.merchant.dto.GiftCardFulfillmentRequest;
import com.example.fulfillmentservice.merchant.exception.MerchantProviderTimeoutException;
import com.example.fulfillmentservice.merchant.exception.MerchantProviderUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RequiredArgsConstructor
@Component
public class RestMerchantProviderClient implements MerchantProviderClient {
    private final RestClient merchantProviderRestClient;

    @Retry(name = "merchantProvider")
    @CircuitBreaker(name = "merchantProvider")
    @Override
    public void fulfill(RedemptionRequestedEvent event) {

        var request = new GiftCardFulfillmentRequest();
        request.setCustomerId(event.getCustomerId());
        request.setRedemptionId(event.getRedemptionId());
        request.setRewardIds(event.getRewardIds());

        try {
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
        } catch (HttpServerErrorException.ServiceUnavailable ex) {

            throw new MerchantProviderUnavailableException(
                    "Merchant provider is unavailable",
                    ex
            );

        } catch (ResourceAccessException ex) {

            throw new MerchantProviderTimeoutException(
                    "Merchant provider request timed out",
                    ex
            );
        }
    }
}
