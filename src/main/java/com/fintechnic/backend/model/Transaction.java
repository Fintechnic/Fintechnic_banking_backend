package com.fintechnic.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "transaction")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long TransactionId;

    @ManyToOne
    @JoinColumn(name = "user_id") // tạo ra một biến user_id trong Transaction có reference tới id của class User
    private User user;

    //Field Tpe
    @Enumerated(EnumType.STRING)
    @Column
    private TransactionType transactionType;

    //Field Status
    @Enumerated(EnumType.STRING)
    @Column()
    private TransactionStatus status;

    //Field Amnout
    @Column
    private BigDecimal amount;

    //Field Description
    @Column
    private String description;

    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist  // Gán giá trị mặc định trước khi lưu vào DB
    protected void onCreate() {
        
        if (this.status == null) {
            this.status = TransactionStatus.PENDING; // Mặc định là PENDING
        }
    }
}
