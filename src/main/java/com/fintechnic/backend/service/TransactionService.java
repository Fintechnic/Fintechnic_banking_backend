package com.fintechnic.backend.service;

import com.fintechnic.backend.dto.TransactionDTO;
import com.fintechnic.backend.mapper.TransactionMapper;
import com.fintechnic.backend.model.Transaction;
import com.fintechnic.backend.model.TransactionStatus;
import com.fintechnic.backend.repository.TransactionRepository;
import com.fintechnic.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.transactionMapper = transactionMapper;
    }

    // lấy danh giao dịch theo trang
    public Page<TransactionDTO> getTransactions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findAll(pageable)
                .map(transactionMapper::transactionToTransactionDTO);
    }

    // lấy danh sách giao dịch bằng user id
    public Page<TransactionDTO> getTransactionsByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findByFromWalletUserId(userId, pageable)
                .map(transactionMapper::transactionToTransactionDTO);
    }

    // Cập nhật trạng thái giao dịch (thành công / thất bại)
    public Transaction updateTransactionStatus(Long transactionId, String status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setTransactionStatus(Enum.valueOf(TransactionStatus.class, status.toUpperCase()));

        return transactionRepository.save(transaction);
    }

//    public Transaction transfer(Long fromWalletId, Long toWalletId, BigDecimal amount) {
//        // kiểm tra số dư
//
//    }
}
