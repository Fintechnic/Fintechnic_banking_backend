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

    // người thực hiện
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

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

    // đối với giao dịch chuyển khoảng
    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    private User targetUser;

    // đối với thanh toán hóa đơn
    @Column
    private String billCode;
}
