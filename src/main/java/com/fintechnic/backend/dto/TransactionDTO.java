package com.fintechnic.backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDTO {
    private String transactionId;

    private String senderName;
    private String senderId;

    private String receiverName;
    private String receiverId;

    
    private BigDecimal amount;
    private String description;
    private String status;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String type;
    
}
