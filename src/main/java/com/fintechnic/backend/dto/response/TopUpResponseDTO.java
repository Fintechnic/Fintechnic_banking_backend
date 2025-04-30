package com.fintechnic.backend.dto.response;

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
public class TopUpResponseDTO {

    @NotNull
    private Long agentUserId;                // Bắt buộc khi request

    private String username;
    private BigDecimal newBalance;             
    private String transactionId;            // Mã giao dịch trả về cho UI
    private BigDecimal amount;               // Số tiền nạp
    private String description;              // Mô tả giao dịch
    private String status;                   
    private LocalDateTime createdAt = LocalDateTime.now(); // Thời gian tạo
}
