package com.fintechnic.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bank_accounts")
@Data
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //Link với bảng users
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Column(nullable = false)
    private Double balance = 0.0;

    @Enumerated(EnumType.STRING)
    @Column()
    private AccountStatus status;
}
