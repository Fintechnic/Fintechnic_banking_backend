package com.fintechnic.backend.repository;

import com.fintechnic.backend.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByUserId(Long userId);
    boolean existsByAccountNumber(String accountNumber);
}
