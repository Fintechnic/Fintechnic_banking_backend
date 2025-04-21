package com.fintechnic.backend.controller;

import com.fintechnic.backend.dto.TransactionDTO;
import com.fintechnic.backend.dto.TransferRequestDTO;
import com.fintechnic.backend.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
    @GetMapping("/admin/history")
    public ResponseEntity<Page<TransactionDTO>> getTransactions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

    Page<TransactionDTO> transactions = transactionService.getTransactions(page, size);
    return ResponseEntity.ok(transactions);
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




