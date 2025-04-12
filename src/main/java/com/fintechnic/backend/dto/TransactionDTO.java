package com.fintechnic.backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private String targetUser;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private String type;
}
