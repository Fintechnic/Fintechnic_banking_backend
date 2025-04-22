package com.fintechnic.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fintechnic.backend.dto.QRCodeRequestDTO;
import com.fintechnic.backend.service.QRCodeScannerService;
import io.jsonwebtoken.security.InvalidKeyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.security.auth.login.AccountNotFoundException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/qrcode")
public class QRCodeScannerController {
    private final QRCodeScannerService qrCodeScannerService;

    public QRCodeScannerController(QRCodeScannerService qrCodeScannerService) {
        this.qrCodeScannerService = qrCodeScannerService;
    }

    @PostMapping("/scanner")
    public ResponseEntity<?> scanQRCode(@RequestBody QRCodeRequestDTO request,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println(request.getEncryptedData());
            Map<String, Object> response = qrCodeScannerService.processQRCodeData(
                    request.getEncryptedData(),authHeader
            );
            return ResponseEntity.ok().body(response);
        } catch (JsonProcessingException e) {
            log.error("Invalid token format: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid token format.");
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            log.error("Decryption failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Decryption failed. Invalid token.");
        } catch (AccountNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity.badRequest().body("User not found.");
        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Scan unsuccessful due to an internal error.");
        }
    }
}
