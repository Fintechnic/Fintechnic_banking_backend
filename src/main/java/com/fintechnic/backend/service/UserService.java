package com.fintechnic.backend.service;

import com.fintechnic.backend.dto.UserActionDTO;
import com.fintechnic.backend.dto.UserDTO;
import com.fintechnic.backend.dto.UserListDTO;
import com.fintechnic.backend.model.User;

import com.fintechnic.backend.repository.UserRepository;
import com.fintechnic.backend.util.CryptoUtil;
import com.fintechnic.backend.util.JwtUtil;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final WalletService walletService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 30;


    
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, WalletService walletService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.walletService = walletService;
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

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            user.setEmail(CryptoUtil.encrypt(user.getEmail())); // Mã hóa email
        } catch (Exception e) {
            throw new RuntimeException("Encryption error: " + e.getMessage());
        }

        user.setRole(userRepository.count() == 0 ? "ADMIN" : "USER");

        User registeredUser = userRepository.save(user);

        walletService.createMainWallet(user); // tạo thêm ví cùng với tài khoản

        return registeredUser;
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
        String token = jwtUtil.generateToken(user);

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

    //Dashboard Admin
    
    //Tìm kiếm và danh sách người dùng
    public Page<UserListDTO> getUsers(UserDTO request, int page, int size) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
    
            if (request.getUsername() != null && !request.getUsername().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + request.getUsername().toLowerCase() + "%"));
            }
    
            if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + request.getEmail().toLowerCase() + "%"));
            }
    
            if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("phoneNumber")), "%" + request.getPhoneNumber().toLowerCase() + "%"));
            }
    
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    
        // Tạo PageRequest để phân trang theo username và sắp xếp theo thứ tự tăng dần
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "username"));
    
        // Tìm kiếm người dùng và trả về kết quả dưới dạng Page
        Page<User> userPage = userRepository.findAll(spec, pageRequest);
    
        // Chuyển đổi từ Page<User> thành Page<UserListDTO>
        return userPage.map(this::convertToDTO);
    }
    


    // Phương thức chuyển đổi từ User sang UserListDTO
    private UserListDTO convertToDTO(User user) {
        UserListDTO response = new UserListDTO();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole());  
        response.setAccountLocked(user.getAccountLocked());  
        response.setCreatedAt(user.getCreatedAt()); 
        response.setId(user.getId());
        return response;
    }
    
    //Xem chi tiết người dùng
    public User getUserDetails(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    //Reset password người dùng
    public String resetUserPassword(Long userId, UserActionDTO request) {
        // Kiểm tra người dùng có tồn tại không
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());  // Mã hóa mật khẩu mới
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return "Password for user with ID " + userId + " has been reset successfully.";
    }
    
    

}