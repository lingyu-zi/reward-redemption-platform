package com.example.merchantproviderservice.service;

import com.example.merchantproviderservice.dto.GiftCardFulfillmentRequest;
import com.example.merchantproviderservice.dto.GiftCardFulfillmentResponse;
import com.example.merchantproviderservice.exception.BadRequestException;
import com.example.merchantproviderservice.exception.OutOfStockException;
import com.example.merchantproviderservice.exception.ProviderUnavailableException;
import com.example.merchantproviderservice.fulfillment.FulfillmentScenario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GiftCardFulfillmentService {

    private final Map<String, GiftCardFulfillmentResponse>
            processedRequests = new ConcurrentHashMap<>();

    private final FulfillmentScenario scenario;

    public GiftCardFulfillmentService(
            @Value("${mock-provider.scenario}") FulfillmentScenario scenario) {
        this.scenario = scenario;
    }

    public GiftCardFulfillmentResponse fulfill(
            String idempotencyKey,
            GiftCardFulfillmentRequest request) {

        var existingResponse = processedRequests.get(idempotencyKey);

        if (existingResponse != null) {
            return existingResponse;
        }

        return switch (scenario) {
            case OUT_OF_STOCK ->
                    throw new OutOfStockException(
                            "Gift card is out of stock"
                    );

            case UNAVAILABLE ->
                    throw new ProviderUnavailableException(
                            "Invalid gift card fulfillment request"
                    );

            case BAD_REQUEST ->
                throw new BadRequestException(
                        "Gift card request is invalid"
                );

            case TIMEOUT -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(
                            "Timeout simulation interrupted", e);
                }

                throw new RuntimeException(
                        "Timeout simulation completed without client timeout");
            }

            case SUCCESS -> {
                var confirmationCode =
                        "GC-" + UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

                var response = new GiftCardFulfillmentResponse(
                        request.getRedemptionId(),
                        "SUCCESS",
                        confirmationCode
                );

                processedRequests.put(idempotencyKey, response);

                yield response;
            }
        };
    }
}
