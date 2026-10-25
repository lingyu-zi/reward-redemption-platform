package com.example.fulfillmentservice.merchant;

import com.example.fulfillmentservice.event.RedemptionCompletedEvent;
import org.springframework.stereotype.Component;

@Component
public class RestMerchantProviderClient implements MerchantProviderClient {
    @Override
    public void fulfill(RedemptionCompletedEvent event) {
        throw new RuntimeException("Merchant provider unavailable");
    }
}
