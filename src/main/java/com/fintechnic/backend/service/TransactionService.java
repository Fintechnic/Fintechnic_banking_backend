package com.fintechnic.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fintechnic.backend.dto.TransactionDTO;
import com.fintechnic.backend.dto.UserDTO;
import com.fintechnic.backend.model.Transaction;

import com.fintechnic.backend.repository.TransactionRepository;


@Service
public class TransactionService {
    @Autowired private TransactionRepository transactionRepository;
    
    public TransactionDTO createTransaction(Transaction transaction) {
        Transaction savedTransaction = transactionRepository.save(transaction);
        return new TransactionDTO(
            savedTransaction.getTransactionId(),
            savedTransaction.getType().name(),
            savedTransaction.getStatus().name(),
            savedTransaction.getAmount(),
            savedTransaction.getDescription(),
            savedTransaction.getCreatedAt(),
            new UserDTO(savedTransaction.getUser().getId(), savedTransaction.getUser().getUsername())
        );
    }

    

    

}
