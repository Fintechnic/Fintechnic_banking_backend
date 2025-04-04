package com.fintechnic.backend.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fintechnic.backend.service.TransactionService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fintechnic.backend.dto.TransactionDTO;
import com.fintechnic.backend.dto.UserDTO;
import com.fintechnic.backend.model.Transaction;



@RestController
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {
    @Autowired 
    private TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody @Valid TransactionDTO request) {    
        
        try {
            Transaction transaction = transactionService.createTransaction(
                request.getUserId(), request.getAmount(), request.getType(), request.getDescription()
            );

            TransactionDTO transactionDTO = new TransactionDTO(
                transaction.getTransactionId(),
                transaction.getType().toString(),
                transaction.getStatus().toString(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getCreatedAt(),
                new UserDTO(transaction.getUser().getId(), transaction.getUser().getUsername()),
                transaction.getUser().getId()
            );

            return ResponseEntity.ok(transactionDTO); 
        } 
        catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
            }
        }

}
    



