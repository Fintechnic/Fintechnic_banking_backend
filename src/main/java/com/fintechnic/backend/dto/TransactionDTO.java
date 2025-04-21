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
   

    //Filter
    private LocalDateTime startTime; // thời gian bắt đầu để lọc
    private LocalDateTime endTime;   // thời gian kết thúc để lọc
    private String userId;           // ID của user cần lọc
    private String type;

    
}
