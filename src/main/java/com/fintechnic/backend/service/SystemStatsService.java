package com.fintechnic.backend.service;

import com.fintechnic.backend.repository.WalletRepository;
import org.springframework.stereotype.Service;

import com.fintechnic.backend.dto.SystemStatsDTO;
import com.fintechnic.backend.repository.TransactionRepository;
import com.fintechnic.backend.repository.UserRepository;

import java.math.BigDecimal;

@Service

public class SystemStatsService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public SystemStatsService(UserRepository userRepository, TransactionRepository transactionRepository, WalletRepository walletRepository){
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    public SystemStatsDTO getAllStats(){
        Long totalUsers = userRepository.count();
        long totalTransactions = transactionRepository.count();
        BigDecimal totalBalance = walletRepository.getTotalSystemBalance();
        BigDecimal average = walletRepository.getAverageUserBalance();
        return new SystemStatsDTO(totalUsers, totalTransactions, totalBalance, average);
    }
}
