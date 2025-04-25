package com.fintechnic.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillResponseDTO {
    private String type;
    private String phoneNumber;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private Boolean isPaid;
}
