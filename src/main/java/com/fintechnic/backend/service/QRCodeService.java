package com.fintechnic.backend.service;

import com.fintechnic.backend.repository.UserRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Service
public class QRCodeService {
    private final UserRepository userRepository;

    public QRCodeService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public byte[] generateQRCode(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }

    public String encodingInformation(Map<String, Object> transactionData,
                                      Date expirationDate,
                                      SecretKey secretKey) {
        return Jwts.builder()
                .setClaims(transactionData)
                .setExpiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    public Claims decryptingInformation(SecretKey secretKey, String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
