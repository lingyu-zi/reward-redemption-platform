package com.example.fulfillmentservice.merchant.exception;

public class MerchantProviderUnavailableException extends RuntimeException {
    public MerchantProviderUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
