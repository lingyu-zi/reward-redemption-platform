package com.example.fulfillmentservice.merchant;

import com.example.fulfillmentservice.event.RedemptionRequestedEvent;

public interface MerchantProviderClient {
    void fulfill(RedemptionRequestedEvent event);
}
