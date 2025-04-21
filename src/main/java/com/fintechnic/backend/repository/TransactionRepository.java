package com.fintechnic.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fintechnic.backend.dto.TopUpDTO;
import com.fintechnic.backend.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByFromWalletUserIdOrToWalletUserId(Long fromUserId, Long toUserId, Pageable pageable);
    boolean existsByToWalletUserPhoneNumber(String phoneNumber);
    boolean existsByTransactionCode(String transactionCode);
    
}
