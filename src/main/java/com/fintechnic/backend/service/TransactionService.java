package com.fintechnic.backend.service;


import com.fintechnic.backend.dto.request.WithdrawRequestDTO;
import com.fintechnic.backend.dto.response.TransferResponseDTO;
import com.fintechnic.backend.dto.response.WithdrawResponseDTO;
import com.fintechnic.backend.dto.request.TopUpRequestDTO;
import com.fintechnic.backend.dto.request.TransactionFilterRequestDTO;
import com.fintechnic.backend.dto.response.TopUpResponseDTO;
import com.fintechnic.backend.dto.response.TransactionFilterResponseDTO;
import com.fintechnic.backend.mapper.TransactionMapper;
import com.fintechnic.backend.model.*;
import com.fintechnic.backend.repository.TransactionRepository;
import com.fintechnic.backend.repository.UserRepository;
import com.fintechnic.backend.repository.WalletRepository;

import com.fintechnic.backend.util.CryptoUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

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

    // lấy danh sách giao dịch với đầy đủ thông tin (cho admin)
    public Page<Transaction> getTransactions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findAll(pageable);
    }

    // hiển thị lịch sử giao dịch (cho user)
    public Page<TransferResponseDTO> getTransactionsByUserId(Long userId, int page, int size) {
        Pageable getAll = PageRequest.of(page, size);
        return transactionRepository.findByFromWalletUserIdOrToWalletUserId(userId, userId, getAll)
                .map(transaction -> transactionMapper.transactionToTransactionDTO(transaction, userId));
    }

    // Cập nhật trạng thái giao dịch (thành công / thất bại)
    public Transaction updateTransactionStatus(Long transactionId, String status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setTransactionStatus(Enum.valueOf(TransactionStatus.class, status.toUpperCase()));

        return transactionRepository.save(transaction);
    }

    @Transactional
    public TransferResponseDTO transfer(Long fromUserId, String toPhoneNumber, BigDecimal amount, String description) {
        // kiểm tra ví có tồn tại bằng số điện thoại của người sở hữu ví
        Wallet fromWallet = walletRepository.findByUserId(fromUserId)
                .orElseThrow(() -> new RuntimeException("Source wallet not found"));
        Wallet toWallet = walletRepository.findByUserPhoneNumber(toPhoneNumber)
                .orElseThrow(() -> new RuntimeException("Target wallet not found"));

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

    //Thêm tiền vào tài khoản agent
    @Transactional
    public TopUpResponseDTO addMoneyToAgent(TopUpRequestDTO requestDto) {
        Wallet agentWallet = walletRepository.findByUserPhoneNumber(requestDto.getPhoneNumber()).
                orElseThrow(() -> new RuntimeException("Agent wallet not found"));

        User user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra trạng thái ví agent
        if (agentWallet.getWalletStatus() == WalletStatus.CLOSED ||
            agentWallet.getWalletStatus() == WalletStatus.INACTIVE ||
            agentWallet.getWalletStatus() == WalletStatus.SUSPENDED) {
            throw new RuntimeException("Agent account cannot receive money");
        }

        // Thêm tiền vào ví agent
        BigDecimal newBalance = agentWallet.getBalance().add(requestDto.getAmount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient funds for top-up");
        }
        agentWallet.setBalance(newBalance);
        walletRepository.save(agentWallet);

        // Lưu vào lịch sử giao dịch
        Transaction transaction = Transaction.builder()
                .fromWallet(agentWallet)
                .toWallet(agentWallet)   // Ví người nhận cũng là ví agent
                .amount(requestDto.getAmount())
                .description(requestDto.getDescription())
                .transactionStatus(TransactionStatus.SUCCESS)
                .transactionType(TransactionType.TOP_UP)
                .build();

        transactionRepository.save(transaction);

        // Trả về thông tin giao dịch
        return TopUpResponseDTO.builder()
            .username(user.getUsername())
            .amount(requestDto.getAmount())
            .description(requestDto.getDescription())
            .status("SUCCESS")
            .transactionCode(transaction.getTransactionCode())
            .newBalance(agentWallet.getBalance())
            .createdAt(transaction.getCreatedAt())
            .build();
    }



    // Filter giao dịch 
    public Page<TransactionFilterResponseDTO> filterTransactions(TransactionFilterRequestDTO request) {

        Specification<Transaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getTransactionCode() != null) {
                predicates.add(cb.equal(root.get("transactionCode"), request.getTransactionCode()));
            }
            if (request.getTransactionType() != null) {
                predicates.add(cb.equal(root.get("transactionType"), request.getTransactionType()));
            }
            if (request.getTransactionStatus() != null ) {
                predicates.add(cb.equal(root.get("transactionStatus"), request.getTransactionStatus()));
            }
            if (request.getMinAmount() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), request.getMinAmount()));
            }
            if (request.getMaxAmount() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), request.getMaxAmount()));
            }
            if (request.getKeyword() != null) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + request.getKeyword().toLowerCase() + "%"));
            }
            if (request.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), request.getFromDate()));
            }
            if (request.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), request.getToDate()));
            }
            if (request.getFromWalletId() != null) {
                predicates.add(cb.equal(root.get("fromWallet").get("id"), request.getFromWalletId()));
            }
            if (request.getToWalletId() != null) {
                predicates.add(cb.equal(root.get("toWallet").get("id"), request.getToWalletId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort.Direction direction = "ASC".equalsIgnoreCase(request.getSortDirection())
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, request.getSortBy());
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageRequest);

        // Convert Page<Transaction> -> Page<TransactionResponse>
        return transactionPage.map(this::convertToResponse);
    }

    public WithdrawResponseDTO withdraw(WithdrawRequestDTO request, Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Not enough balance in wallet");
        }
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));

        Transaction transaction = Transaction.builder()
                .transactionStatus(TransactionStatus.SUCCESS)
                .transactionType(TransactionType.WITHDRAW)
                .amount(request.getAmount())
                .toWallet(wallet)
                .fromWallet(wallet)
                .build();

        walletRepository.save(wallet);
        transactionRepository.save(transaction);

        return WithdrawResponseDTO.builder()
                .amount(request.getAmount().negate())
                .status(String.valueOf(TransactionStatus.SUCCESS))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private TransactionFilterResponseDTO convertToResponse(Transaction transaction) {
        TransactionFilterResponseDTO response = new TransactionFilterResponseDTO();
        response.setTransactionCode(transaction.getTransactionCode());
        response.setTransactionType(transaction.getTransactionType());
        response.setTransactionStatus(transaction.getTransactionStatus());
        response.setAmount(transaction.getAmount());
        response.setDescription(transaction.getDescription());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setFromWalletId(transaction.getFromWallet() != null ? transaction.getFromWallet().getId() : null);
        response.setToWalletId(transaction.getToWallet() != null ? transaction.getToWallet().getId() : null);
        return response;
    }
}
