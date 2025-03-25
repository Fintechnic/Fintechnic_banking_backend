package com.fintechnic.backend.controller;

import com.fintechnic.backend.model.Transaction;
import com.fintechnic.backend.service.ExpenseTrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/expense")
public class ExpenseTrackingController {
    private final ExpenseTrackingService expenseTrackingService;

    public ExpenseTrackingController(ExpenseTrackingService expenseTrackingService) {
        this.expenseTrackingService = expenseTrackingService;
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getPaymentHistory(@RequestParam Long userId) {
        List<Transaction> transactions = expenseTrackingService.getTransactionList(userId);
        if (transactions == null) {
            return  ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(transactions);
    }
}
