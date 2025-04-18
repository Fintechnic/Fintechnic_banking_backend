package com.fintechnic.backend.controller;

import com.fintechnic.backend.dto.TopUpDTO;
import com.fintechnic.backend.dto.TransactionDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fintechnic.backend.service.TransactionService;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/transaction/admin")
public class TopUpController {
    private final TransactionService transactionService;

    //Constructor 
    public TopUpController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @PostMapping("/topup")
    public ResponseEntity<TransactionDTO> addMoneyToAgent(
        @RequestBody TopUpDTO request) {

            try { 
                TransactionDTO transactionDTO = transactionService.addMoneyToAgent(request.getAgentUserId(), request.getAmount(), request.getDescription());
                return new ResponseEntity<>(transactionDTO, HttpStatus.CREATED);
            } catch (RuntimeException e) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

            }
        }
}


