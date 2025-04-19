package com.fintechnic.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // chỉ trả về field nào có giá trị
public class TopUpDTO {

    // Dùng cho cả request và response
    @NotNull
    private Long agentUserId;                // Bắt buộc khi request

    // Response-only
    private String agentFullName;            // Optional: chỉ dùng để hiển thị trên UI
    private BigDecimal newBalance;           // Sau khi nạp
    private String transactionId;            // Mã giao dịch trả về cho UI

    // Request + response
    private BigDecimal amount;               // Số tiền nạp
    private String description;              // Mô tả giao dịch
    private String status;                   
    private LocalDateTime createdAt = LocalDateTime.now(); // Thời gian tạo
}
