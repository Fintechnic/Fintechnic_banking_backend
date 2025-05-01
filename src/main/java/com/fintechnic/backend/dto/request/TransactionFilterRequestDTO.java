package com.fintechnic.backend.dto.request;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fintechnic.backend.model.TransactionStatus;
import com.fintechnic.backend.model.TransactionType;

@Data
public class TransactionFilterRequestDTO {
    private String transactionCode;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String keyword;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Long fromWalletId;
    private Long toWalletId;

    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
    private int page = 0;
    private int size = 20;
}

