package com.fintechnic.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintechnic.backend.util.CryptoUtil;
import com.fintechnic.backend.util.JwtUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeService {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public QRCodeService(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    public byte[] generateQRCode(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }

    public String createQRCodeContents(String authHeader) throws Exception {
        Long userId = jwtUtil.extractUserIdFromToken(authHeader);
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(5);

        long expirationTimeStamp = expirationDate
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", userId);
        userInfo.put("exp", expirationTimeStamp);

        String jsonString = objectMapper.writeValueAsString(userInfo);

        // mã hóa dữ liệu JSON
        return CryptoUtil.encrypt(jsonString);
    }
}
