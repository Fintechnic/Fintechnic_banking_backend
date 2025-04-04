package com.fintechnic.backend.controller;

import com.fintechnic.backend.model.Transaction;
import com.fintechnic.backend.service.ExpenseTrackingService;
import com.fintechnic.backend.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense")
public class ExpenseTrackingController {
    private final ExpenseTrackingService expenseTrackingService;
    private final JwtUtil jwtUtil;

    public ExpenseTrackingController(ExpenseTrackingService expenseTrackingService, JwtUtil jwtUtil) {
        this.expenseTrackingService = expenseTrackingService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getPaymentHistory(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // xóa chữ bearer trong header
        Long userId = jwtUtil.extractUserId(token);

        List<Transaction> transactions = expenseTrackingService.getTransactionList(userId);
        return ResponseEntity.ok().body(transactions);
    }
}
