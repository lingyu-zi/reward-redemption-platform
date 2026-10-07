package com.example.rewardredemption.transaction;

import com.example.rewardredemption.transaction.dto.CreateTransactionRequest;
import com.example.rewardredemption.transaction.dto.TransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "merchant.id", target = "merchantId")
    TransactionResponse toTransactionResponse(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionReference", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "merchant", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Transaction toTransactionEntity(CreateTransactionRequest request);
}
