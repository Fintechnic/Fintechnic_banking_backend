package com.fintechnic.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintechnic.backend.model.User;
import com.fintechnic.backend.service.QRCodeService;
import com.fintechnic.backend.util.CryptoUtil;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import java.util.Map;
import java.util.Optional;

@Slf4j
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
            String transactionInformation = CryptoUtil.decrypt(token);
            log.info("Decrypted data: {}", transactionInformation);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> json = objectMapper.readValue(transactionInformation, Map.class);

            Object userIdObj = json.get("userId");
            long userId;
            if (userIdObj instanceof Integer) {
                userId = ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof Long) {
                userId = (Long) userIdObj;
            } else {
                return ResponseEntity.badRequest()
                        .body("Invalid userId format in token.");
            }

            Optional<User> user = qrCodeService.getUserById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("User not found.");
            }

            return ResponseEntity.ok()
                    .body("Scan successful! " + json);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid token format.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid userId format in token.");

        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            return ResponseEntity.badRequest()
                    .body("Decryption failed. Invalid token.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Scan unsuccessful due to an internal error.");
        }
    }
}
