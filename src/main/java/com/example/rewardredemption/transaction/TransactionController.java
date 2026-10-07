package com.example.rewardredemption.transaction;

import com.example.rewardredemption.transaction.dto.CreateTransactionRequest;
import com.example.rewardredemption.transaction.dto.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request){
        var transaction = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PutMapping("/{transactionId}/complete")
    public TransactionResponse completeTransaction(
            @PathVariable Long transactionId){
        return transactionService.completeTransaction(transactionId);
    }

    @GetMapping("/{transactionId}")
    public TransactionResponse getTransactionById(
            @PathVariable Long transactionId){
        return transactionService.getTransactionById(transactionId);
    }

    @GetMapping("/reference/{transactionReference}")
    public TransactionResponse getTransactionByReference(
            @PathVariable UUID transactionReference){
        return transactionService.getTransactionByReference(transactionReference);
    }

    @GetMapping("/customer/{customerId}")
    public List<TransactionResponse> getTransactionsByCustomerId(
            @PathVariable Long customerId) {
        return transactionService.getTransactionsByCustomerId(customerId);
    }
}
