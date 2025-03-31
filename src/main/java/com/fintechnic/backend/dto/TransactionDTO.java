package com.fintechnic.backend.dto;

import java.time.LocalDateTime;

public class TransactionDTO {
    private Long transactionId;
    private String type;
    private String status;
    private Double amount;
    private String description;
    private LocalDateTime createdAt;
    private UserDTO user;

    public TransactionDTO(Long transactionId, String type, String status, Double amount,
                          String description, LocalDateTime createdAt, UserDTO user) {
        this.transactionId = transactionId;
        this.type = type;
        this.status = status;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
        this.user = user;
    }

    public Long getTransactionId() { return transactionId; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public Double getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public UserDTO getUser() { return user; }
}
