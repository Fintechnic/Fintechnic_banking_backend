package com.fintechnic.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fintechnic.backend.service.TransactionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fintechnic.backend.dto.TransactionDTO;
import com.fintechnic.backend.model.Transaction;



@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired 
    private TransactionService transactionService;

    @PostMapping("create")
    public TransactionDTO createTransaction(@RequestBody Transaction transaction) {        
        return transactionService.createTransaction(transaction);
    }
    


}
