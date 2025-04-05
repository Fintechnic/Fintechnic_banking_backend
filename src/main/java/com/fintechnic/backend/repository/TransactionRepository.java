package com.fintechnic.backend.repository;

import com.fintechnic.backend.model.Transaction;
import com.fintechnic.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
}
