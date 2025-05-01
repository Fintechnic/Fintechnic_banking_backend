package com.fintechnic.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

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
    private BigDecimal balance = BigDecimal.ZERO;

    // user sở hữu ví
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // loại ví
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletType walletType;

    // trạng thái ví
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus walletStatus;

    private BigDecimal interestRate; // có thể null, chỉ dành cho ví tiết kiệm

    // lần cuối ví được cập nhật
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastUpdated;
}
