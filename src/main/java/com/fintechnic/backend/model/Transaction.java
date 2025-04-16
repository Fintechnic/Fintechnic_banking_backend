package com.fintechnic.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // mã giao dịch
    @Column(unique = true, nullable = false)
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

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;

    // ví thao tác
    @ManyToOne
    @JoinColumn(name = "from_wallet_id", nullable = false)
    private Wallet fromWallet;

    // ví đích đến
    @ManyToOne
    @JoinColumn(name = "to_wallet_id", nullable = false)
    private Wallet toWallet;

    @PrePersist
    private void generateTransactionCode() {
        if (transactionCode == null) {
            transactionCode = "TX-" + UUID.randomUUID().toString();
        }
    }
}
