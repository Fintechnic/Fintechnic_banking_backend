package com.fintechnic.backend.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fintechnic.backend.model.TransactionStatus;
import com.fintechnic.backend.model.TransactionType;

@Data
public class TransactionFilterResponseDTO {
    private String transactionCode;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
    private Long fromWalletId;
    private Long toWalletId;
}

