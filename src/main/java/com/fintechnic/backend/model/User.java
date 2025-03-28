package com.fintechnic.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true)
    private String email;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private String role = "USER";


    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    @Column
    private Boolean accountLocked = false;

    @Column
    private LocalDateTime lastFailedLogin;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> activeTokens = new HashSet<>();


}