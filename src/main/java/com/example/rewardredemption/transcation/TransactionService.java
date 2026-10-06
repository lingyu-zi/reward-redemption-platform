package com.example.rewardredemption.transcation;

import com.example.rewardredemption.customer.CustomerRepository;
import com.example.rewardredemption.exception.BadRequestException;
import com.example.rewardredemption.exception.ResourceNotFoundException;
import com.example.rewardredemption.merchant.MerchantRepository;
import com.example.rewardredemption.transcation.dto.CreateTransactionRequest;
import com.example.rewardredemption.transcation.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    private final TransactionMapper transactionMapper;

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
        var transaction = transactionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Transaction not found with id: " + id));
        if (transaction.getStatus() == TransactionStatus.COMPLETED) {
            throw new BadRequestException(
                    "Transaction is already completed");
        } else if (transaction.getStatus() == TransactionStatus.FAILED) {
            throw new BadRequestException(
                    "Transaction cannot be completed from status: "  + transaction.getStatus());
        }
        transaction.setStatus(TransactionStatus.COMPLETED);
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
