package com.fintechnic.backend.service;


import com.fintechnic.backend.dto.WalletSummaryDTO;
import com.fintechnic.backend.model.User;
import com.fintechnic.backend.model.Wallet;
import com.fintechnic.backend.model.WalletStatus;
import com.fintechnic.backend.model.WalletType;
import com.fintechnic.backend.repository.TransactionRepository;
import com.fintechnic.backend.repository.UserRepository;
import com.fintechnic.backend.repository.WalletRepository;


import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
        
@Service
public class WalletService {
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet createWallet(User user, WalletType walletType) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("User must be created before creating wallet");
        }
        if (walletType == null) {
            throw new IllegalArgumentException("Wallet type cannot be null");
        }

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setWalletStatus(WalletStatus.ACTIVE);
        wallet.setWalletType(walletType);

        return walletRepository.save(wallet);
    }

    public WalletSummaryDTO getWalletSummary() {
        BigDecimal total = walletRepository.getTotalSystemBalance();
        BigDecimal average = walletRepository.getAverageUserBalance();
        
        return new WalletSummaryDTO(total, average);
    }

    public BigDecimal getBalance(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId);
        return wallet.getBalance();
    }
}
