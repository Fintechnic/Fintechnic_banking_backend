package com.fintechnic.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // mã giao dịch
    @Column
    private String transactionCode;

    // loại giao dịch
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    // Trạng thái giao dịch
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus transactionStatus;

    // số tiền
    @Column(nullable = false)
    private BigDecimal amount;

    // phần mô tả giao dịch
    @Column
    private String description;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    // đối với thanh toán hóa đơn
    @Column
    private String billCode;

    // ví thao tác
    @ManyToOne
    @JoinColumn(name = "from_wallet_id", nullable = false)
    private Wallet fromWallet;

    // ví đích đến
    @ManyToOne
    @JoinColumn(name = "to_wallet_id", nullable = false)
    private Wallet toWallet;
}
