package com.fintechnic.backend.repository;

import com.fintechnic.backend.model.BankAccount;
import com.fintechnic.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByUser(User user);
    BankAccount findByAccountNumber(String accountNumber);
}
