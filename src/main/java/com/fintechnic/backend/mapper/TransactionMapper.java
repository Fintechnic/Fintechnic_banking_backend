package com.fintechnic.backend.mapper;

import com.fintechnic.backend.dto.TransactionDTO;
import com.fintechnic.backend.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "toWallet.user.username", target = "targetUser")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "transactionStatus", target = "status")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "transactionType", target = "type")
    TransactionDTO transactionToTransactionDTO(Transaction transaction);
}
