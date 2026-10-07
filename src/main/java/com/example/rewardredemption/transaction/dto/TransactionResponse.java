package com.example.rewardredemption.transaction.dto;

import com.example.rewardredemption.transaction.TransactionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class TransactionResponse {
    private Long id;
    private UUID transactionReference;
    private Long customerId;
    private Long merchantId;
    private BigDecimal amount;
    private TransactionStatus status;
    private Instant createdAt;
}
