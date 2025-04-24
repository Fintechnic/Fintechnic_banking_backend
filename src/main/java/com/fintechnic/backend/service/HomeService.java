package com.fintechnic.backend.service;

import com.fintechnic.backend.dto.HomeDTO;
import com.fintechnic.backend.model.User;
import com.fintechnic.backend.model.Wallet;
import com.fintechnic.backend.repository.UserRepository;
import com.fintechnic.backend.util.JwtUtil;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;

@Service
public class HomeService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final WalletService walletService;

    public HomeService(JwtUtil jwtUtil, UserRepository userRepository, WalletService walletService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.walletService = walletService;
    }

    public HomeDTO getHomeInformation(String authHeader) throws AccountNotFoundException {
        Long userId = jwtUtil.extractUserIdFromToken(authHeader);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AccountNotFoundException("User not found"));
        BigDecimal balance = walletService.getBalance(userId);

        return new HomeDTO(user.getUsername(), balance);
    }
}
