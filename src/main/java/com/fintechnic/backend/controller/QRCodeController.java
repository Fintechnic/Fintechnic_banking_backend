package com.fintechnic.backend.controller;

import com.fintechnic.backend.service.QRCodeService;
import com.google.zxing.WriterException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/qrcode")
public class QRCodeController {

    private final QRCodeService qrCodeService;
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public QRCodeController(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/myqrcode")
    public ResponseEntity<byte[]> getQRCode(@RequestParam Long userId) throws IOException, WriterException {
        Date expirationDate = new Date(System.currentTimeMillis() + 5 * 60 * 1000);

        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("userId", userId);
        transactionData.put("exp", expirationDate);

        // mã hóa dữ thành jwt token
        String encodedData = qrCodeService.encodingInformation(transactionData, expirationDate, secretKey);

        byte[] qrCode = qrCodeService.generateQRCode(encodedData, 200, 200);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }
}