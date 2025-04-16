package com.fintechnic.backend.service;

import com.fintechnic.backend.dto.TransactionDTO;
import com.fintechnic.backend.mapper.TransactionMapper;
import com.fintechnic.backend.model.*;
import com.fintechnic.backend.repository.TransactionRepository;
import com.fintechnic.backend.repository.UserRepository;
import com.fintechnic.backend.repository.WalletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;
    private final WalletRepository walletRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, TransactionMapper transactionMapper, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.transactionMapper = transactionMapper;
        this.walletRepository = walletRepository;
    }

    // lấy danh sách giao dịch với đầy đủ thông tin
    public Page<Transaction> getTransactions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findAll(pageable);
    }

    // hiển thị lịch sử giao dịch
    public Page<TransactionDTO> getTransactionsByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findByFromWalletUserIdOrToWalletUserId(userId, userId, pageable)
                .map(transaction -> transactionMapper.transactionToTransactionDTO(transaction, userId));
    }

    // Cập nhật trạng thái giao dịch (thành công / thất bại)
    public Transaction updateTransactionStatus(Long transactionId, String status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setTransactionStatus(Enum.valueOf(TransactionStatus.class, status.toUpperCase()));

        return transactionRepository.save(transaction);
    }

    public TransactionDTO transfer(Long fromUserId, String toPhoneNumber, BigDecimal amount, String description) {
        // kiểm tra ví có tồn tại bằng số điện thoại của người sở hữu ví
        Wallet fromWallet = walletRepository.findByUserId(fromUserId);
        Wallet toWallet = walletRepository.findByUserPhoneNumber(toPhoneNumber);

        User user = userRepository.findById(fromUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // kiểm tra status của ví
        if (fromWallet.getWalletStatus() == WalletStatus.CLOSED ||
            fromWallet.getWalletStatus() == WalletStatus.INACTIVE ||
            fromWallet.getWalletStatus() == WalletStatus.SUSPENDED) {

            throw new RuntimeException("Your account cannot do transaction");
        }

        if (toWallet.getWalletStatus() == WalletStatus.CLOSED ||
            toWallet.getWalletStatus() == WalletStatus.INACTIVE ||
            toWallet.getWalletStatus() == WalletStatus.SUSPENDED) {

            throw new RuntimeException("Cannot do transaction with target account");
        }

        // kiểm tra số dư hiện tại của ví
        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Your balance is not enough");
        }

        // thực hiện giao dịch
        fromWallet.setBalance(fromWallet.getBalance().subtract(amount)); // trừ tiền ví của tài khoản gửi
        toWallet.setBalance(toWallet.getBalance().add(amount));// cộng tiền ví của tài khoản nhận
        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        // lưu vào lịch sử giao dịch
        Transaction transaction = Transaction.builder()
                .fromWallet(fromWallet)
                .toWallet(toWallet)
                .amount(amount)
                .description(description)
                .transactionStatus(TransactionStatus.SUCCESS)
                .transactionType(TransactionType.TRANSFER)
                .build();

        transactionRepository.save(transaction);

        return transactionMapper.transactionToTransactionDTO(transaction, fromUserId);

    }
}
