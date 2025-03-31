package com.fintechnic.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fintechnic.backend.model.Transaction;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
