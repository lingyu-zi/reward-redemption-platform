package com.example.fulfillmentservice.merchant;

import com.example.fulfillmentservice.event.RedemptionCompletedEvent;

public interface MerchantProviderClient {
    void fulfill(RedemptionCompletedEvent event);
}
