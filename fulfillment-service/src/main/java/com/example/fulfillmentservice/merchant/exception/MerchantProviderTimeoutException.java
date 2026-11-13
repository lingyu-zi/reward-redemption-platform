package com.example.fulfillmentservice.merchant.exception;

public class MerchantProviderTimeoutException extends RuntimeException {
    public MerchantProviderTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
