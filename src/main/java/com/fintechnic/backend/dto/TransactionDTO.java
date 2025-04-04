package com.fintechnic.backend.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

public class TransactionDTO {
    private Long transactionId;

    @NotNull(message = "Type cannot be null")
    private String type;

    private String status;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be bigger than 0")
    private Double amount;

    private String description;
    private LocalDateTime createdAt;
    

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    public TransactionDTO(Long transactionId, String type, String status, Double amount,
                          String description, LocalDateTime createdAt, UserDTO user, Long userId) {
        this.transactionId = transactionId;
        this.type = type;
        this.status = status;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public Long getTransactionId() { return transactionId; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public Double getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getUserId() {return userId;}
}
