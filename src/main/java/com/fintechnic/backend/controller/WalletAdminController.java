package com.fintechnic.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintechnic.backend.dto.WalletSummaryDTO;
import com.fintechnic.backend.service.WalletService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/wallet")
public class WalletAdminController {
    private final WalletService walletService;

    @GetMapping("/summary")
    public ResponseEntity<WalletSummaryDTO> getWalletSummary() {
        return ResponseEntity.ok(walletService.getWalletSummary());
    }
}
