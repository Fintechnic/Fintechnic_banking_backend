package com.fintechnic.backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopUpDTO {
    private Long agentUserId; 
    private BigDecimal amount; 
    private String description; 
    private String status;
    private LocalDateTime createdAt = LocalDateTime.now();
}
