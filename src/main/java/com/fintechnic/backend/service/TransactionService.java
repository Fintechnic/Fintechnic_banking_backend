package com.fintechnic.backend.service;

import com.fintechnic.backend.model.Transaction;
import com.fintechnic.backend.model.TransactionStatus;
import com.fintechnic.backend.model.TransactionType;
import com.fintechnic.backend.model.User;
import com.fintechnic.backend.repository.TransactionRepository;
import com.fintechnic.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    // Tạo giao dịch
    @Transactional
    public Transaction createTransaction(Long userId, Double amount, String type, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (amount <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setType(Enum.valueOf(TransactionType.class, type.toUpperCase())); // Chuyển String thành Enum
        transaction.setStatus(TransactionStatus.PENDING); // Mặc định là Pending
        transaction.setDescription(description);

        return transactionRepository.save(transaction);
    }

    // Lấy tất cả giao dịch của một user
    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    // Cập nhật trạng thái giao dịch (thành công / thất bại)
    @Transactional
    public Transaction updateTransactionStatus(Long transactionId, String status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setStatus(Enum.valueOf(TransactionStatus.class, status.toUpperCase()));

        return transactionRepository.save(transaction);
    }
}
