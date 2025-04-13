package com.fintechnic.backend.mapper;

import com.fintechnic.backend.dto.TransactionDTO;
import com.fintechnic.backend.model.Transaction;
import com.fintechnic.backend.service.TransactionService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "counterparty", expression = "java(currentUserId.equals(transaction.getFromWallet().getUser().getId()) ? transaction.getToWallet().getUser().getUsername() : transaction.getFromWallet().getUser().getUsername())")
    @Mapping(target = "amount", expression = "java(currentUserId.equals(transaction.getFromWallet().getUser().getId()) ? transaction.getAmount().negate() : transaction.getAmount())")
    @Mapping(source = "transactionStatus", target = "status")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "transactionType", target = "type")
    TransactionDTO transactionToTransactionDTO(Transaction transaction, @Context Long currentUserId);
}
