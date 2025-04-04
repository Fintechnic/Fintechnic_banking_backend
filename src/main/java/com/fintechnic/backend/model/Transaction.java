package com.fintechnic.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_history")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private TransactionType transactionType;

    @Column
    private BigDecimal amount;

    @Column
    private String transactionDescription;

    @Column
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id") // tạo ra một biến user_id trong Transaction có reference tới id của class User
    private User user;
}
