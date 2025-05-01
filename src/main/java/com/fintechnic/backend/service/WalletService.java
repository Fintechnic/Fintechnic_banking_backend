package com.fintechnic.backend.service;


import com.fintechnic.backend.dto.WalletSummaryDTO;
import com.fintechnic.backend.dto.request.WalletRequestDTO;
import com.fintechnic.backend.model.User;
import com.fintechnic.backend.model.Wallet;
import com.fintechnic.backend.model.WalletStatus;
import com.fintechnic.backend.model.WalletType;
import com.fintechnic.backend.repository.WalletRepository;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

import org.springframework.stereotype.Service;
        
@Service
public class WalletService {
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet createMainWallet(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("User must be created before creating wallet");
        }

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setWalletType(WalletType.MAIN);
        wallet.setWalletStatus(WalletStatus.ACTIVE);

        return walletRepository.save(wallet);
    }

    public WalletSummaryDTO getWalletSummary() {
        BigDecimal total = walletRepository.getTotalSystemBalance();
        BigDecimal average = walletRepository.getAverageUserBalance();
        
        return new WalletSummaryDTO(total, average);
    }

    public BigDecimal getBalance(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        return wallet.getBalance();
    }


    public Wallet searchWallet(WalletRequestDTO request) {
        Specification<Wallet> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getUserId() != null) {
                predicates.add(cb.equal(root.get("user").get("id"), request.getUserId()));
            }

            // Lọc theo username nếu có
            if (request.getUsername() != null && !request.getUsername().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("user").get("username")), "%" + request.getUsername().toLowerCase() + "%"));
            }

            // Lọc theo email nếu có
            if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("user").get("email")), "%" + request.getEmail().toLowerCase() + "%"));
            }

            // Lọc theo phoneNumber nếu có
            if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("user").get("phoneNumber")), "%" + request.getPhoneNumber().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Tìm ví agent dựa trên Specification
        return walletRepository.findAll(spec).stream().findFirst().orElseThrow(() -> new RuntimeException("Agent wallet not found"));
    }
}

