package com.fintechnic.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillRequestDTO {
    private String type;
    private String phoneNumber;
    private BigDecimal amount;
}
