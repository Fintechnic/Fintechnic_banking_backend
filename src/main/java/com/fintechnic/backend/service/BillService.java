package com.fintechnic.backend.service;

import com.fintechnic.backend.dto.request.BillRequestDTO;
import com.fintechnic.backend.dto.response.BillResponseDTO;
import com.fintechnic.backend.dto.response.TransferResponseDTO;
import com.fintechnic.backend.mapper.BillMapper;
import com.fintechnic.backend.mapper.TransactionMapper;
import com.fintechnic.backend.model.*;
import com.fintechnic.backend.repository.BillRepository;
import com.fintechnic.backend.repository.TransactionRepository;
import com.fintechnic.backend.repository.UserRepository;
import com.fintechnic.backend.repository.WalletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillService {
    private final BillRepository billRepository;
    private final BillMapper billMapper;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TransactionMapper transactionMapper;
    private final UserRepository userRepository;

    public BillService(BillRepository billRepository, BillMapper billMapper, TransactionRepository transactionRepository, WalletRepository walletRepository, TransactionMapper transactionMapper, UserRepository userRepository) {
        this.billRepository = billRepository;
        this.billMapper = billMapper;
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.transactionMapper = transactionMapper;
        this.userRepository = userRepository;
    }

    // hiện bill chưa đóng cho người dùng
    public Page<BillResponseDTO> getBillsByUserId(Long userId, int page, int size) {
        Pageable getAll = PageRequest.of(page, size);
        return billRepository.findByUserIdAndIsPaid(userId, false, getAll)
                .map(bill -> billMapper.billToBillResponseDTO(bill));
    }

    // thanh toán bill
    @Transactional
    public TransferResponseDTO payBill(Long billId, Long userId) {
        Bill bill = billRepository.findByIdAndIsPaid(billId, false)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found or already paid"));
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        if (wallet.getBalance().compareTo(bill.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(bill.getAmount()));
        bill.setIsPaid(true);

        Transaction transaction = Transaction.builder()
                .fromWallet(wallet)
                .toWallet(wallet)
                .amount(bill.getAmount())
                .transactionType(TransactionType.BILL_PAYMENT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .build();

        transactionRepository.save(transaction);

        return transactionMapper.transactionToTransactionDTO(transaction, userId);
    }

    @Transactional
    public BillResponseDTO createBill(BillRequestDTO request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Bill bill = new Bill();
        bill.setUser(user);
        bill.setType(BillType.valueOf(request.getType()));
        bill.setAmount(request.getAmount());
        bill.setPhoneNumber(user.getPhoneNumber());

        billRepository.save(bill);

        return billMapper.billToBillResponseDTO(bill);
    }
}
