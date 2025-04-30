package com.fintechnic.backend.controller;

import org.springframework.http.HttpStatus;
import com.fintechnic.backend.dto.request.TopUpRequestDTO;
import com.fintechnic.backend.dto.request.WalletRequestDTO;
import com.fintechnic.backend.dto.response.TopUpResponseDTO;
import com.fintechnic.backend.model.Wallet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fintechnic.backend.service.TransactionService;
import com.fintechnic.backend.service.WalletService;

@RestController
@RequestMapping("/api/transaction/admin")
public class TopUpController {
    private final WalletService walletService;
    private final TransactionService transactionService;

    

    //Constructor 
    public TopUpController(TransactionService transactionService, WalletService walletService){
        this.transactionService = transactionService;
        this.walletService = walletService;
    }

    // Tìm ví trước khi top up
    @PostMapping("/search-wallet")
    public ResponseEntity<Wallet> searchWallet(@RequestBody WalletRequestDTO request) {
        // Tìm ví agent theo các tham số
        Wallet wallet = walletService.searchWallet(request);
        return ResponseEntity.ok(wallet);
    }

    // Nạp tiền ví agent
    @PostMapping("/top-up")
    public ResponseEntity<TopUpResponseDTO> addMoneyToAgent(@RequestBody TopUpRequestDTO requestDto) {
        // Thực hiện nạp tiền vào ví agent
        TopUpResponseDTO response = transactionService.addMoneyToAgent(requestDto);
        return ResponseEntity.ok(response);
    }
}


