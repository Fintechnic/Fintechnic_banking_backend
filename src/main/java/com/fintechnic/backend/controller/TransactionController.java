package com.fintechnic.backend.controller;

import java.util.List;

import com.fintechnic.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fintechnic.backend.service.TransactionService;


import com.fintechnic.backend.model.Transaction;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
public class TransactionController {
    private TransactionService transactionService;
    private JwtUtil jwtUtil;

    // lấy danh sách giao dịch
    @RequestMapping
    public ResponseEntity<List<Transaction>> getTransactions(@RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.substring(7);
            Long userId = jwtUtil.extractUserId(token);
            List<Transaction> transaction = transactionService.getTransactions(userId);

            return ResponseEntity.ok().body(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}




