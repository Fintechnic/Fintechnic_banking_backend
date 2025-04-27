package com.fintechnic.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.fintechnic.backend.dto.TopUpDTO;
import com.fintechnic.backend.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    Page<Transaction> findByFromWalletUserIdOrToWalletUserId(Long fromUserId, Long toUserId, Pageable pageable);
    boolean existsByToWalletUserPhoneNumber(String phoneNumber);
    boolean existsByTransactionCode(String transactionCode);
}
