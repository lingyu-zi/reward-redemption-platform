package com.example.transactionservice.transaction;

import com.example.transactionservice.dto.CreateTransactionRequest;
import com.example.transactionservice.dto.TransactionResponse;
import com.example.transactionservice.event.TransactionCompletedEvent;
import com.example.transactionservice.event.TransactionEventProducer;
import com.example.transactionservice.exception.BadRequestException;
import com.example.transactionservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionEventProducer transactionEventProducer;

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        var transaction = transactionMapper.toTransactionEntity(request);

        transaction.setTransactionReference(UUID.randomUUID());
        transaction.setStatus(TransactionStatus.PENDING);

        var savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toTransactionResponse(savedTransaction);
    }

    @Transactional
    public TransactionResponse completeTransaction(Long id) {
        // load transaction
        var transaction = transactionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Transaction not found with id: " + id));
        // validate status
        if (transaction.getStatus() == TransactionStatus.COMPLETED) {
            throw new BadRequestException(
                    "Transaction is already completed");
        } else if (transaction.getStatus() == TransactionStatus.FAILED) {
            throw new BadRequestException(
                    "Transaction cannot be completed from status: "  + transaction.getStatus());
        }
        // set status to COMPLETED
        transaction.setStatus(TransactionStatus.COMPLETED);
        // get points change
        var pointsChange = transaction.getAmount().multiply(BigDecimal.valueOf(1.5))
                .setScale(0, RoundingMode.DOWN).longValue();
        // build TransactionCompletedEvent
        var event = new TransactionCompletedEvent();
        event.setTransactionId(transaction.getId());
        event.setCustomerId(transaction.getCustomerId());
        event.setMerchantId(transaction.getMerchantId());
        event.setTransactionReference(transaction.getTransactionReference());
        event.setAmount(transaction.getAmount());
        event.setPointsChange(pointsChange);
        // publish to Kafka
        transactionEventProducer.publishTransactionCompleted(event);
        return transactionMapper.toTransactionResponse(transaction);
    }

    public TransactionResponse getTransactionById(Long id) {
        var transaction = transactionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Transaction not found with id: " + id));
        return transactionMapper.toTransactionResponse(transaction);
    }

    public TransactionResponse getTransactionByReference(UUID transactionReference) {
        var transaction = transactionRepository.findByTransactionReference(transactionReference).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Transaction not found with reference: " + transactionReference));
        return transactionMapper.toTransactionResponse(transaction);
    }

    public List<TransactionResponse> getTransactionsByCustomerId(Long customerId) {
        return transactionRepository.findByCustomerId(customerId).stream()
                .map(transactionMapper::toTransactionResponse)
                .toList();
    }
}
