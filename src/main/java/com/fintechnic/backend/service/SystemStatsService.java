package com.fintechnic.backend.service;

import org.springframework.stereotype.Service;

import com.fintechnic.backend.dto.SystemStatsDTO;
import com.fintechnic.backend.repository.TransactionRepository;
import com.fintechnic.backend.repository.UserRepository;

@Service

public class SystemStatsService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    public SystemStatsService(UserRepository userRepository, TransactionRepository transactionRepository){
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }
    public SystemStatsDTO getAllStats(){
        Long totalUsers = userRepository.count();
        long totalTransactions = transactionRepository.count();
        return new SystemStatsDTO(totalUsers, totalTransactions); 


    }
 
}
