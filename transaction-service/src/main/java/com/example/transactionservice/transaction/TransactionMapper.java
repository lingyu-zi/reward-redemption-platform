package com.example.transactionservice.transaction;

import com.example.transactionservice.dto.CreateTransactionRequest;
import com.example.transactionservice.dto.TransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionResponse toTransactionResponse(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionReference", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Transaction toTransactionEntity(CreateTransactionRequest request);
}
