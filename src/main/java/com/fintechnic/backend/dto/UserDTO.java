package com.fintechnic.backend.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    Long id;
    String username;
    String email;
    String phoneNumber;
    BigDecimal balance;
}
