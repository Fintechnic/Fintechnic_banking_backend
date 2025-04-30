package com.fintechnic.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopUpRequestDTO {

    @NotNull
    private Long agentUserId;  // Bắt buộc khi request

    private String userName;  

    private String email;

    private String phoneNumber;
    
    @NotNull
    private BigDecimal amount; // Số tiền nạp

    private String description; // Mô tả giao dịch
}

