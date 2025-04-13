package com.fintechnic.backend.controller;

import com.fintechnic.backend.dto.TransactionDTO;
import com.fintechnic.backend.dto.TransferRequestDTO;
import com.fintechnic.backend.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fintechnic.backend.service.TransactionService;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    private final TransactionService transactionService;
    private final JwtUtil jwtUtil;

    public TransactionController(TransactionService transactionService, JwtUtil jwtUtil) {
        this.transactionService = transactionService;
        this.jwtUtil = jwtUtil;
    }

    // lấy danh sách giao dịch
    @GetMapping("/history")
    public ResponseEntity<Page<TransactionDTO>> getTransactions(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Missing or invalid Authorization header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String token = authHeader.substring(7);
            Long userId = jwtUtil.extractUserId(token);
            if (userId == null) {
                log.error("Invalid userId extracted from token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Page<TransactionDTO> transactions = transactionService.getTransactionsByUserId(userId, page, size);
            return ResponseEntity.ok().body(transactions);
        } catch (JwtException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("Error fetching transactions: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionDTO> createTransfer(
            @RequestBody @Valid TransferRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {

        // userId của người dùng hiện tại đang muốn gửi tiền
        String token = authHeader.substring(7);
        Long currentUserId;
        try {
            currentUserId = jwtUtil.extractUserId(token);
            if (currentUserId == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token: User ID not found");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        TransactionDTO response = transactionService.transfer(
                currentUserId,
                request.getPhoneNumber(),
                request.getAmount(),
                request.getDescription()
        );

        return ResponseEntity.ok().body(response);
    }
}




