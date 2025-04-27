package com.fintechnic.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet")
@Data
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // số dư
    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal balance = BigDecimal.ZERO; // số dư

    // user sở hữu ví
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // trạng thái ví
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus walletStatus;

    // lần cuối ví được cập nhật
    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();
}
