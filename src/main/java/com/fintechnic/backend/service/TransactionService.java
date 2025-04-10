package com.fintechnic.backend.service;

import com.fintechnic.backend.model.Transaction;
import com.fintechnic.backend.model.TransactionStatus;
import com.fintechnic.backend.model.User;
import com.fintechnic.backend.repository.TransactionRepository;
import com.fintechnic.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public List<Transaction> getTransactions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.findByUserId(userId);
    }

    // Cập nhật trạng thái giao dịch (thành công / thất bại)
    @Transactional
    public Transaction updateTransactionStatus(Long transactionId, String status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setTransactionStatus(Enum.valueOf(TransactionStatus.class, status.toUpperCase()));

        return transactionRepository.save(transaction);
    }
}
