package com.fintechnic.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

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

    @Pattern(regexp = "^0[0-9]{9,10}$", message = "Invalid phone number")
    @Column(nullable = false, unique = true)
    private String phoneNumber; // số điện thoại

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

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