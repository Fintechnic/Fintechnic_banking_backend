package com.fintechnic.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class TransactionDTO {
    private final Long transactionId;

    @NotNull(message = "Type cannot be null")
    private String type;

    private String status;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be bigger than 0")
    private BigDecimal amount;

    private String description;
    private LocalDateTime createdAt;
    

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    public TransactionDTO(Long transactionId, String type, String status, BigDecimal amount,
                          String description, LocalDateTime createdAt, UserDTO user, Long userId) {
        this.transactionId = transactionId;
        this.type = type;
        this.status = status;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
        this.userId = userId;
    }

}
