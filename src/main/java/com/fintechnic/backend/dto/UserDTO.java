package com.fintechnic.backend.dto;

import lombok.Getter;

@Getter
public class UserDTO {
    private Long id;
    private String username;

    public UserDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

}
