package com.fintechnic.backend.mapper;

import com.fintechnic.backend.dto.TransactionDTO;
import com.fintechnic.backend.model.Transaction;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    // Dành cho user — cần currentUserId để xử lý âm/dương số tiền
    @Mappings({
        @Mapping(source = "transaction.transactionCode", target = "transactionId"),

        @Mapping(target = "senderName",
                 expression = "java(transaction.getFromWallet().getUser().getUsername())"),
        @Mapping(target = "senderId",
                 expression = "java(transaction.getFromWallet().getUser().getId().toString())"),

        @Mapping(target = "receiverName",
                 expression = "java(transaction.getToWallet().getUser().getUsername())"),
        @Mapping(target = "receiverId",
                 expression = "java(transaction.getToWallet().getUser().getId().toString())"),

        @Mapping(target = "amount",
                 expression = "java(currentUserId.equals(transaction.getFromWallet().getUser().getId())"
                            + " ? transaction.getAmount().negate()"
                            + " : transaction.getAmount())"),

        @Mapping(source = "transaction.description", target = "description"),
        @Mapping(source = "transaction.transactionStatus", target = "status"),
        @Mapping(source = "transaction.createdAt", target = "createdAt"),
        @Mapping(source = "transaction.transactionType", target = "type")

    })
    TransactionDTO transactionToTransactionDTO(
        Transaction transaction,
        @Context Long currentUserId
    );

    // Dành cho admin — không cần xử lý âm/dương
    @Mappings({
        @Mapping(source = "transaction.transactionCode", target = "transactionId"),

        @Mapping(target = "senderName",
                 expression = "java(transaction.getFromWallet().getUser().getUsername())"),
        @Mapping(target = "senderId",
                 expression = "java(transaction.getFromWallet().getUser().getId().toString())"),

        @Mapping(target = "receiverName",
                 expression = "java(transaction.getToWallet().getUser().getUsername())"),
        @Mapping(target = "receiverId",
                 expression = "java(transaction.getToWallet().getUser().getId().toString())"),

        @Mapping(source = "transaction.amount", target = "amount"),

        @Mapping(source = "transaction.description", target = "description"),
        @Mapping(source = "transaction.transactionStatus", target = "status"),
        @Mapping(source = "transaction.createdAt", target = "createdAt"),
        @Mapping(source = "transaction.transactionType", target = "type")

    })
    TransactionDTO transactionToTransactionDTO(Transaction transaction);
}
