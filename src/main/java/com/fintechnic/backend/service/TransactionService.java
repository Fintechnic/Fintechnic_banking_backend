package com.fintechnic.backend.service;

import com.fintechnic.backend.dto.TopUpDTO;
import com.fintechnic.backend.dto.TransactionDTO;
import com.fintechnic.backend.dto.TransactionFilterRequestDTO;
import com.fintechnic.backend.dto.TransactionFilterResponseDTO;
import com.fintechnic.backend.mapper.TransactionMapper;
import com.fintechnic.backend.model.*;
import com.fintechnic.backend.repository.TransactionRepository;
import com.fintechnic.backend.repository.UserRepository;
import com.fintechnic.backend.repository.WalletRepository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import java.util.stream.Collectors;

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

    //Thêm tiền vào tài khoản agent
    public TopUpDTO addMoneyToAgent(TopUpDTO requesDto){
        Wallet agentWallet = walletRepository.findByUserId(requesDto.getAgentUserId());
        if (agentWallet == null){
            throw new RuntimeException("Agent wallet is not found");
        }

        if (agentWallet.getWalletStatus() == WalletStatus.CLOSED ||
            agentWallet.getWalletStatus() == WalletStatus.INACTIVE ||
            agentWallet.getWalletStatus() == WalletStatus.SUSPENDED) {
            throw new RuntimeException("Agent account cannot receive money");
        }

        //Thêm tiền vào ví agent
        BigDecimal newBalance = agentWallet.getBalance().add(requesDto.getAmount());
        agentWallet.setBalance(newBalance);
        walletRepository.save(agentWallet);



        // Lưu vào lịch sử giao dịch
        Transaction transaction = Transaction.builder()
                .fromWallet(agentWallet) // Tạm coi ví của agent là ví nguồn
                .toWallet(agentWallet)   // Ví người nhận cũng là ví agent (vì chỉ thêm tiền vào ví agent)
                .amount(requesDto.getAmount())
                .description(requesDto.getDescription())
                .transactionStatus(TransactionStatus.SUCCESS)
                .transactionType(TransactionType.TOP_UP) // Loại giao dịch là nạp tiền
                .build();

        transactionRepository.save(transaction);

        
        
        return TopUpDTO.builder()
            .agentUserId(requesDto.getAgentUserId())
            .agentFullName(requesDto.getAgentFullName())
            .amount(requesDto.getAmount())
            .description(requesDto.getDescription())
            .status("SUCCESS")
            .transactionId(transaction.getId().toString())
            .newBalance(newBalance)
            .createdAt(transaction.getCreatedAt())
            .build();

    }

    public Page<TransactionDTO> getAllTransactions(int page, int size) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllTransactions'");
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
            if (request.getStatuses() != null && !request.getStatuses().isEmpty()) {
                predicates.add(root.get("transactionStatus").in(request.getStatuses()));
            }
            if (request.getMinAmount() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), request.getMinAmount()));
            }
            if (request.getMaxAmount() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), request.getMaxAmount()));
            }
            if (request.getKeyword() != null) {
                predicates.add(cb.like(cb.lower(root.get("description")),
                        "%" + request.getKeyword().toLowerCase() + "%"));
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
