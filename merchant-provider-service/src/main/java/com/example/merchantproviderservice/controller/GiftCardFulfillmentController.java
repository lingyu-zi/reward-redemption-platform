package com.example.merchantproviderservice.controller;

import com.example.merchantproviderservice.dto.GiftCardFulfillmentRequest;
import com.example.merchantproviderservice.dto.GiftCardFulfillmentResponse;
import com.example.merchantproviderservice.service.GiftCardFulfillmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/gift-cards")
public class GiftCardFulfillmentController {
    private final GiftCardFulfillmentService giftCardFulfillmentService;

    @PostMapping("/fulfill")
    public ResponseEntity<GiftCardFulfillmentResponse> fulfill(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody GiftCardFulfillmentRequest request){

        var response = giftCardFulfillmentService
                .fulfill(idempotencyKey, request);
        return ResponseEntity.ok(response);
    }
}
