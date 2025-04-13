package com.fintechnic.backend.repository;

import com.fintechnic.backend.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByUserId(Long userId);
    Wallet findByUserPhoneNumber(String phoneNumber);
}
