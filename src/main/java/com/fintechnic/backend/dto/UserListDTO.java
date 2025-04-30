package com.fintechnic.backend.dto;

import java.time.LocalDateTime;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListDTO {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String role;
    private Boolean accountLocked;
    private int failedLoginAttempts;
    private LocalDateTime createdAt;

}
