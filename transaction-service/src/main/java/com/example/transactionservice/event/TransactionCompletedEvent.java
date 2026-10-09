package com.example.transactionservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionCompletedEvent {
    private Long transactionId;
    private UUID transactionReference;
    private Long customerId;
    private Long merchantId;
    private BigDecimal amount;
    private Long pointsChange;
}
