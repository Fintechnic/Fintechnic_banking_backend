package com.fintechnic.backend.controller;

import com.fintechnic.backend.service.QRCodeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qrcode")
public class QRCodeController {

    private final QRCodeService qrCodeService;

    public QRCodeController(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/myqrcode")
    public ResponseEntity<byte[]> getQRCode(@RequestHeader("Authorization") String authHeader) throws Exception {
        // tạo và mã hóa dữ liệu json
        String encryptedData = qrCodeService.createQRCodeContents(authHeader);

        // tạo mã QR
        byte[] qrCode = qrCodeService.generateQRCode(encryptedData, 200, 200);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }
}