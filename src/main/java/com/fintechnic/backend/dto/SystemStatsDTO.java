package com.fintechnic.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemStatsDTO {
    private Long totalUsers;
    private Long totalTransactions;

}
