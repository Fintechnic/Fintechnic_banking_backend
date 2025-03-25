package com.fintechnic.backend.service;

import com.fintechnic.backend.model.Transaction;
import com.fintechnic.backend.model.User;
import com.fintechnic.backend.repository.ExpenseTrackingRepository;
import com.fintechnic.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

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
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return null;
        }

        return expenseTrackingRepository.findByUserId(userId);
    }
}
