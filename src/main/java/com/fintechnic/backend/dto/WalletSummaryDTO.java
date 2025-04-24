package com.fintechnic.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WalletSummaryDTO {
    private BigDecimal totalBalance;
    private BigDecimal averageBalance;

}
