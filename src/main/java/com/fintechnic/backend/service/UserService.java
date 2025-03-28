package com.fintechnic.backend.service;

import com.fintechnic.backend.model.User;
import com.fintechnic.backend.repository.UserRepository;
import com.fintechnic.backend.util.CryptoUtil;
import com.fintechnic.backend.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 30;

    public UserService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder, 
                      JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    
        try {
            user.setEmail(CryptoUtil.decrypt(user.getEmail())); // Giải mã email trước khi trả về
        } catch (Exception e) {
            throw new RuntimeException("Decryption error: " + e.getMessage());
        }
    
        return user;
    }
    

    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            user.setEmail(CryptoUtil.encrypt(user.getEmail())); // Mã hóa email
        } catch (Exception e) {
            throw new RuntimeException("Encryption error: " + e.getMessage());
        }

        user.setRole(userRepository.count() == 0 ? "ADMIN" : "USER");

        return userRepository.save(user);
    }

    public String loginUser(String username, String password) {
        System.out.println("Login attempt for username: " + username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("User not found for username: " + username);
                    return new RuntimeException("User not found");
                });

        // Kiểm tra nếu tài khoản đã bị khóa
        if (user.getAccountLocked()) {
            if (user.getLastFailedLogin().plusMinutes(LOCK_DURATION_MINUTES).isAfter(LocalDateTime.now())) {
                throw new RuntimeException("Account locked. Try again later.");
            } else {
                // Mở khóa sau thời gian quy định
                user.setAccountLocked(false);
                user.setFailedLoginAttempts(0);
            }
        }

        // Check password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            user.setLastFailedLogin(LocalDateTime.now());

            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setAccountLocked(true);
                userRepository.save(user);
                throw new RuntimeException("Too many failed attempts. Account locked.");
            }

            userRepository.save(user);
            System.out.println("Invalid password for username: " + username);
            throw new RuntimeException("Invalid password");
        }

        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(username);

        Set<String> activeTokens = user.getActiveTokens();
        if (!activeTokens.isEmpty()) {
            activeTokens.clear();
        }

        activeTokens.add(token);
        user.setActiveTokens(activeTokens);
        userRepository.save(user);

        return token;

    }

    public void logoutUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.getActiveTokens().clear();
        userRepository.save(user);
    }

    public void unlockUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }    

    public void setUserRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
