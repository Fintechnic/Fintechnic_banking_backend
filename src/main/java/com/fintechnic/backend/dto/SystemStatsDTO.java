package com.fintechnic.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SystemStatsDTO {
    private Long totalUsers;
    private Long totalTransactions;
    private BigDecimal totalBalance;
    private BigDecimal averageBalance;
}
