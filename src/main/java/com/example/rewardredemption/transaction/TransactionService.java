package com.example.rewardredemption.transaction;

import com.example.rewardredemption.customer.CustomerRepository;
import com.example.rewardredemption.exception.BadRequestException;
import com.example.rewardredemption.exception.ResourceNotFoundException;
import com.example.rewardredemption.merchant.MerchantRepository;
import com.example.rewardredemption.transaction.dto.CreateTransactionRequest;
import com.example.rewardredemption.transaction.dto.TransactionResponse;
import com.example.rewardredemption.transaction.event.TransactionCompletedEvent;
import com.example.rewardredemption.transaction.event.TransactionEventProducer;
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
    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionEventProducer transactionEventProducer;

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        var transaction = transactionMapper.toTransactionEntity(request);
        var transactionRef = UUID.randomUUID();
        var customer = customerRepository.findById(request.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Customer not found with id: " + request.getCustomerId()));
        var merchant = merchantRepository.findById(request.getMerchantId()).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Merchant not found with id: " + request.getMerchantId()));
        transaction.setTransactionReference(transactionRef);
        transaction.setCustomer(customer);
        transaction.setMerchant(merchant);
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
        event.setCustomerId(transaction.getCustomer().getId());
        event.setMerchantId(transaction.getMerchant().getId());
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
