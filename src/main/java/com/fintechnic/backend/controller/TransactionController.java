package com.fintechnic.backend.controller;

import com.fintechnic.backend.dto.request.WithdrawRequestDTO;
import com.fintechnic.backend.dto.response.TransferResponseDTO;
import com.fintechnic.backend.dto.request.TransferRequestDTO;
import com.fintechnic.backend.dto.response.WithdrawResponseDTO;
import com.fintechnic.backend.model.Transaction;
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
@RequestMapping("/api")
public class TransactionController {
    private final TransactionService transactionService;
    private final JwtUtil jwtUtil;

    public TransactionController(TransactionService transactionService, JwtUtil jwtUtil) {
        this.transactionService = transactionService;
        this.jwtUtil = jwtUtil;
    }

    // lấy danh sách giao dịch của admin
    @GetMapping("/admin/transaction/history")
    public ResponseEntity<Page<Transaction>> getTransactions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        try {
            Page<Transaction> transactions = transactionService.getTransactions(page, size);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // lấy lịch sử giao dịch của người dùng
    @GetMapping("transaction/history")
    public ResponseEntity<Page<TransferResponseDTO>> getTransactions(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Long userId = jwtUtil.extractUserIdFromToken(authHeader);
            if (userId == null) {
                log.error("Invalid userId extracted from token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Page<TransferResponseDTO> transactions = transactionService.getTransactionsByUserId(userId, page, size);
            return ResponseEntity.ok().body(transactions);
        } catch (JwtException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("Error fetching transactions: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("transaction/transfer")
    public ResponseEntity<TransferResponseDTO> createTransfer(
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

        TransferResponseDTO response = transactionService.transfer(
                currentUserId,
                request.getPhoneNumber(),
                request.getAmount(),
                request.getDescription()
        );

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/transaction/withdraw")
    public ResponseEntity<WithdrawResponseDTO> withdraw(@RequestHeader("Authorization") String authHeader,
                                                        @RequestBody WithdrawRequestDTO request) {
        Long userId = jwtUtil.extractUserIdFromToken(authHeader);
        WithdrawResponseDTO response = transactionService.withdraw(request, userId);

        return ResponseEntity.ok(response);
    }
}




