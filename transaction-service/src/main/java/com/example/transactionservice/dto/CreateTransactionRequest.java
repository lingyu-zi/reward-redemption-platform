package com.example.transactionservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTransactionRequest {
    @NotNull(message = "Customer id is required")
    private Long customerId;
    @NotNull(message = "Merchant id is required")
    private Long merchantId;
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}
