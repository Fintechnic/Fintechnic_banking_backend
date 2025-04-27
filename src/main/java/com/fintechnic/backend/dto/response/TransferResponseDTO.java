package com.fintechnic.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TransferResponseDTO {
    private String counterparty; // bên còn lại (A đối với B, B đối với A)
    private BigDecimal amount;
    private String description;
    private String status;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String type;
}
