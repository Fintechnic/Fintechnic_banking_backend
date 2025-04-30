package com.fintechnic.backend.dto.request;

import lombok.Data;

@Data
public class WalletRequestDTO {
    private Long userId;  // ID của agent user (tùy chọn)
    private String username;   // Tìm kiếm theo username (tùy chọn)
    private String email;      // Tìm kiếm theo email (tùy chọn)
    private String phoneNumber; // Tìm kiếm theo số điện thoại (tùy chọn)
}

