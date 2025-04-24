package com.fintechnic.backend.repository;

import com.fintechnic.backend.model.Wallet;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByUserId(Long userId);
    Wallet findByUserPhoneNumber(String phoneNumber);
  
    @Query("SELECT SUM(w.balance) FROM Wallet w")
    BigDecimal getTotalSystemBalance();

    @Query("SELECT AVG(sub.totalBalance) FROM (SELECT SUM(w.balance) AS totalBalance FROM Wallet w GROUP BY w.user.id) sub")
    BigDecimal getAverageUserBalance();
}
