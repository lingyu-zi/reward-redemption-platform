package com.example.redemptionservice.redemption;

import com.example.redemptionservice.redemption.dto.RedemptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/redemptions")
public class RedemptionController {
    private final RedemptionService redemptionService;

    @PostMapping("/cart/{cartId}")
    public ResponseEntity<RedemptionResponse> redeem(
            @PathVariable Long cartId){
        var redemption = redemptionService.redeem(cartId);
        return ResponseEntity.status(HttpStatus.CREATED).body(redemption);
    }

    @GetMapping("/{redemptionId}")
    public RedemptionResponse getRedemptionById(
            @PathVariable Long redemptionId) {
        return  redemptionService.getRedemptionById(redemptionId);
    }

    @GetMapping("/customer/{customerId}")
    public List<RedemptionResponse> getRedemptionsByCustomerId(
            @PathVariable Long customerId){
        return redemptionService.getRedemptionsByCustomerId(customerId);
    }
}
