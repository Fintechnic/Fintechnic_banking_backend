package com.fintechnic.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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

    @Column
    private String accountNumber; // số tài khoản

    @Column
    private BigDecimal balance = BigDecimal.valueOf(0); // số dư

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

    @JsonIgnore // ngăn việc json bị đệ quy
    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions;
}