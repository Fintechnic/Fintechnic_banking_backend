package com.fintechnic.backend.repository;

import com.fintechnic.backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseTrackingRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTransactionsByUserId(Long userId);
}
