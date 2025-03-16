package com.fintechnic.backend.controller;

import com.fintechnic.backend.service.QRCodeService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.Date;

@RestController
@RequestMapping("/qrcode")
public class QRCodeScannerController {

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final QRCodeService qrCodeService;

    public QRCodeScannerController(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/scanner")
    public ResponseEntity<?> scanQRCode(@RequestParam String token) {
        try {
            Claims transactionInformation = qrCodeService.decryptingInformation(secretKey, token);

            long userId = Long.parseLong(transactionInformation.get("userId").toString());
            Date expirationDate = transactionInformation.getExpiration();

            if (expirationDate.before(new Date())) {
                return ResponseEntity.badRequest()
                        .body("Expired QR code");
            }

            return ResponseEntity.ok()
                    .body("Scan successfully " + userId);

        } catch (SignatureException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid key");
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest()
                    .body("Expired token");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Something went wrong");
        }
    }
}
