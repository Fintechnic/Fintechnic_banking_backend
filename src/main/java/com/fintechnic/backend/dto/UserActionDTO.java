package com.fintechnic.backend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserActionDTO {
    private String newPassword;  // Mật khẩu mới (cho reset mật khẩu)
    private String newRole;      // Role mới (cho thay đổi quyền)
}

