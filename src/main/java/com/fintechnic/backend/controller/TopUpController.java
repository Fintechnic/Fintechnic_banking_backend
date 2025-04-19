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


@RestController
@RequestMapping("/api/transaction/admin")
public class TopUpController {
    private final TransactionService transactionService;

    //Constructor 
    public TopUpController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @PostMapping("/topup")
    public ResponseEntity<TopUpDTO> addMoneyToAgent(
        @RequestBody TopUpDTO requestDto) {

            try { 
                TopUpDTO responseDto = transactionService.addMoneyToAgent(requestDto);
                return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
            } catch (RuntimeException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
}


