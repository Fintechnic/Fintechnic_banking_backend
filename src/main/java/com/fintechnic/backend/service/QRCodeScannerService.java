package com.fintechnic.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintechnic.backend.model.User;
import com.fintechnic.backend.repository.UserRepository;
import com.fintechnic.backend.util.CryptoUtil;
import com.fintechnic.backend.util.JwtUtil;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Service
public class QRCodeScannerService {
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public QRCodeScannerService(ObjectMapper objectMapper, JwtUtil jwtUtil, UserRepository userRepository) {
        this.objectMapper = objectMapper;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    public Map<String, Object> processQRCodeData(String encryptedData, String authHeader) throws Exception {
        String transactionInformation = CryptoUtil.decrypt(encryptedData);

        Map<String, Object> json = objectMapper.readValue(
                transactionInformation,
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)
        );

        // xác nhận expiration date của mã QR
        validateExpiration(json);

        // lấy userId
        Long sourceUserId = jwtUtil.extractUserIdFromToken(authHeader);
        Long destinationUserId = extractUserId(json);

        User sourceUser = userRepository.findById(sourceUserId)
                .orElseThrow(() -> new AccountNotFoundException("Source user not found"));

        User destinationUser = userRepository.findById(destinationUserId)
                .orElseThrow(() -> new AccountNotFoundException("Destination user not found"));

        return Map.of(
                "userId", sourceUser.getId(),
                "phoneNumber", destinationUser.getPhoneNumber()
        );
    }

    private void validateExpiration(Map<String, Object> json) {
        Object expObj = json.get("exp");
        if (!(expObj instanceof Number)) {
            throw new IllegalArgumentException("Expiration time is missing or invalid");
        }
        long exp = ((Number) expObj).longValue();
        if (LocalDateTime.ofInstant(Instant.ofEpochMilli(exp), ZoneId.systemDefault())
                .isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("QR code has expired");
        }
    }

    private Long extractUserId(Map<String, Object> json) {
        Object userIdObj = json.get("userId");
        if (!(userIdObj instanceof Number)) {
            throw new IllegalArgumentException("Invalid or missing userId in QR code");
        }
        return ((Number) userIdObj).longValue();
    }
}
