package com.fintechnic.backend.service;

import com.fintechnic.backend.model.Transaction;
import com.fintechnic.backend.model.User;
import com.fintechnic.backend.repository.ExpenseTrackingRepository;
import com.fintechnic.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.naming.TimeLimitExceededException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseTrackingService {
    private final ExpenseTrackingRepository expenseTrackingRepository;
    private final UserRepository userRepository;

    public ExpenseTrackingService(ExpenseTrackingRepository expenseTrackingRepository, UserRepository userRepository) {
        this.expenseTrackingRepository = expenseTrackingRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> getTransactionList(Long userId) {
        // check if userId exist
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return expenseTrackingRepository.findTransactionsByUserId(userId);
    }

    public void processTransaction(Transaction transaction, User user) {
        BigDecimal balance;
        switch (transaction.getTransactionType()) {
            case TRANSFER:
                balance = user.getBalance().subtract(transaction.getAmount());
                user.setBalance(balance);
                break;
            case RECEIVE:
                balance = user.getBalance().add(transaction.getAmount());
                user.setBalance(balance);
                break;
        }
        userRepository.save(user);
    }
}
