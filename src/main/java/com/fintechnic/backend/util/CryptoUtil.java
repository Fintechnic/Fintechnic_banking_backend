package com.fintechnic.backend.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class CryptoUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16; // độ dài của IV
    private static final String SECRET_KEY = "0123456789abcdef0123456789abcdef"; // Khóa 256-bit
    private static final SecureRandom RANDOM = new SecureRandom(); // khởi tạo IV ngẫu nhiên

    public static String encrypt(String data) {
        try {
            if (data == null) {
                throw new IllegalArgumentException("Data cannot be null");
            }

            // tạo IV ngẫu nhiên
            byte[] iv = new byte[IV_LENGTH];
            RANDOM.nextBytes(iv);

            // khởi tạo đối tượng cipher
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // gộp IV và thông tin mã hóa
            byte[] combined = new byte[IV_LENGTH + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(encryptedBytes, 0, combined, IV_LENGTH, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: ", e);
        }
    }

    public static String decrypt(String encryptedData) throws Exception {
        if (encryptedData == null) {
            throw new IllegalArgumentException("Encrypted data cannot be null");
        }

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        if (decodedBytes.length < IV_LENGTH) {
            throw new IllegalArgumentException("Invalid encrypted data length");
        }
        byte[] iv = Arrays.copyOfRange(decodedBytes, 0, IV_LENGTH); // lấy IV ra từ mảng byte
        byte[] encryptedBytes = Arrays.copyOfRange(decodedBytes, IV_LENGTH, decodedBytes.length); // lấy thông tin bị mã

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
