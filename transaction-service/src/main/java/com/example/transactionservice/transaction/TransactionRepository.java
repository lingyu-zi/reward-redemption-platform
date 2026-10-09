package com.example.transactionservice.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionReference(UUID transactionReference);

    List<Transaction> findByCustomerId(Long customerId);
}