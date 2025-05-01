RestTemplateConfig.java:
package com.fintechnic.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```
SecurityConfig.java:
package com.fintechnic.backend.config;

import com.fintechnic.backend.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN") // Chỉ ADMIN được truy cập
                .anyRequest().authenticated())

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
    
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```
WebConfig.java:
package com.fintechnic.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```
controller/AuthController.java:
package com.fintechnic.backend.controller;

import com.fintechnic.backend.model.User;
import com.fintechnic.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        try {
            String token = userService.loginUser(credentials.get("username"), credentials.get("password"));
            User user = userService.findByUsername(credentials.get("username")); // Fetch user details
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("role", user.getRole()); // Include role in response
            response.put("accountLocked", user.getAccountLocked());
    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> request) {
        try {
            userService.logoutUser(request.get("username"));
            return ResponseEntity.ok("User logged out successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/unlock")
    public ResponseEntity<?> unlockUser(@RequestBody Map<String, String> request) {
        try {
            userService.unlockUser(request.get("username"));
            return ResponseEntity.ok("User account unlocked successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
```
controller/BillController.java:
package com.fintechnic.backend.controller;

import com.fintechnic.backend.dto.request.BillRequestDTO;
import com.fintechnic.backend.dto.response.BillResponseDTO;
import com.fintechnic.backend.dto.response.TransferResponseDTO;
import com.fintechnic.backend.service.BillService;
import com.fintechnic.backend.util.JwtUtil;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BillController {
    private final BillService billService;
    private final JwtUtil jwtUtil;

    public BillController(BillService billService, JwtUtil jwtUtil) {
        this.billService = billService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/bills")
    public ResponseEntity<Page<BillResponseDTO>> getUserBills(@RequestHeader("Authorization") String authHeader,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {

        try {
            Long userId = jwtUtil.extractUserIdFromToken(authHeader);
            Page<BillResponseDTO> bills = billService.getBillsByUserId(userId, page, size);
            return ResponseEntity.ok(bills);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/bills/{billId}/pay")
    public ResponseEntity<TransferResponseDTO> payBill(@PathVariable Long billId,
                                                       @RequestHeader("Authorization") String authHeader) {

        try {
            Long userId = jwtUtil.extractUserIdFromToken(authHeader);
            TransferResponseDTO payment = billService.payBill(billId, userId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/admin/new-bill")
    public ResponseEntity<BillResponseDTO> createBill(@RequestBody BillRequestDTO request) {
        try {
            BillResponseDTO response = billService.createBill(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
```
controller/HomeController.java:
package com.fintechnic.backend.controller;

import com.fintechnic.backend.dto.HomeDTO;
import com.fintechnic.backend.service.HomeService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;

@Slf4j
@RestController
public class HomeController {
    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/api/home")
    public ResponseEntity<HomeDTO> getHomeInfo(@RequestHeader("Authorization") String authHeader) {
        try {
            HomeDTO homeInfo = homeService.getHomeInformation(authHeader);
            return ResponseEntity.ok(homeInfo);
        } catch (AccountNotFoundException e) {
            log.info("User not found");
            return ResponseEntity.badRequest().build();
        }
    }
}
```
controller/QRCodeController.java:
package com.fintechnic.backend.controller;

import com.fintechnic.backend.service.QRCodeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qrcode")
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
```
controller/QRCodeScannerController.java:
package com.fintechnic.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fintechnic.backend.dto.request.QRCodeRequestDTO;
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
```
controller/SystemStatsController.java:
package com.fintechnic.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.fintechnic.backend.dto.SystemStatsDTO;
import com.fintechnic.backend.service.SystemStatsService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/")
public class SystemStatsController {
    private final SystemStatsService systemStatsService;

    @GetMapping("/systemstats")
    public ResponseEntity<SystemStatsDTO> getAllStats(){
        return ResponseEntity.ok(systemStatsService.getAllStats());
        
    }

}
```
controller/TopUpController.java:
package com.fintechnic.backend.controller;

import com.fintechnic.backend.dto.request.TopUpRequestDTO;
import com.fintechnic.backend.dto.request.WalletRequestDTO;
import com.fintechnic.backend.dto.response.TopUpResponseDTO;
import com.fintechnic.backend.model.Wallet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fintechnic.backend.service.TransactionService;
import com.fintechnic.backend.service.WalletService;

@RestController
@RequestMapping("/api/admin/transaction")
public class TopUpController {
    private final WalletService walletService;
    private final TransactionService transactionService;

    

    //Constructor 
    public TopUpController(TransactionService transactionService, WalletService walletService){
        this.transactionService = transactionService;
        this.walletService = walletService;
    }

    // Tìm ví trước khi top up
    @PostMapping("/search-wallet")
    public ResponseEntity<Wallet> searchWallet(@RequestBody WalletRequestDTO request) {
        // Tìm ví agent theo các tham số
        Wallet wallet = walletService.searchWallet(request);
        return ResponseEntity.ok(wallet);
    }

    // Nạp tiền ví agent
    @PostMapping("/top-up")
    public ResponseEntity<TopUpResponseDTO> addMoneyToAgent(@RequestBody TopUpRequestDTO requestDto) {
        // Thực hiện nạp tiền vào ví agent
        TopUpResponseDTO response = transactionService.addMoneyToAgent(requestDto);
        return ResponseEntity.ok(response);
    }
}
```
controller/TransactionController.java:
package com.fintechnic.backend.controller;

import com.fintechnic.backend.dto.request.WithdrawRequestDTO;
import com.fintechnic.backend.dto.response.TransferResponseDTO;
import com.fintechnic.backend.dto.request.TransferRequestDTO;
import com.fintechnic.backend.dto.response.WithdrawResponseDTO;
import com.fintechnic.backend.dto.request.TransactionFilterRequestDTO;
import com.fintechnic.backend.model.Transaction;
import com.fintechnic.backend.util.JwtUtil;
import com.fintechnic.backend.dto.response.TransactionFilterResponseDTO;

import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fintechnic.backend.service.TransactionService;
import org.springframework.web.server.ResponseStatusException;


@Slf4j
@RestController
@RequestMapping("/api")
public class TransactionController {
    private final TransactionService transactionService;
    private final JwtUtil jwtUtil;

    public TransactionController(TransactionService transactionService, JwtUtil jwtUtil) {
        this.transactionService = transactionService;
        this.jwtUtil = jwtUtil;
    }

    // lấy danh sách giao dịch
    @GetMapping("/admin/history")
    public ResponseEntity<Page<Transaction>> getTransactions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        try {
            Page<Transaction> transactions = transactionService.getTransactions(page, size);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // lấy lịch sử giao dịch của người dùng
    @GetMapping("transaction/history")
    public ResponseEntity<Page<TransferResponseDTO>> getTransactions(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Long userId = jwtUtil.extractUserIdFromToken(authHeader);
            if (userId == null) {
                log.error("Invalid userId extracted from token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Page<TransferResponseDTO> transactions = transactionService.getTransactionsByUserId(userId, page, size);
            return ResponseEntity.ok().body(transactions);
        } catch (JwtException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("Error fetching transactions: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("transaction/transfer")
    public ResponseEntity<TransferResponseDTO> createTransfer(
            @RequestBody @Valid TransferRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {

        // userId của người dùng hiện tại đang muốn gửi tiền
        String token = authHeader.substring(7);
        Long currentUserId;
        try {
            currentUserId = jwtUtil.extractUserId(token);
            if (currentUserId == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token: User ID not found");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        TransferResponseDTO response = transactionService.transfer(
                currentUserId,
                request.getPhoneNumber(),
                request.getAmount(),
                request.getDescription()
        );

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/transaction/withdraw")
    public ResponseEntity<WithdrawResponseDTO> withdraw(@RequestHeader("Authorization") String authHeader,
                                                        @RequestBody WithdrawRequestDTO request) {
        Long userId = jwtUtil.extractUserIdFromToken(authHeader);
        WithdrawResponseDTO response = transactionService.withdraw(request, userId);

        return ResponseEntity.ok(response);
    }
  
    @PostMapping("/admin/filter")
    public Page<TransactionFilterResponseDTO> filterTransactions(@Valid @RequestBody TransactionFilterRequestDTO request){
        return transactionService.filterTransactions(request);
    }
}
```
controller/UserController.java:
package com.fintechnic.backend.controller;

import com.fintechnic.backend.dto.UserActionDTO;
import com.fintechnic.backend.dto.UserDTO;
import com.fintechnic.backend.dto.UserListDTO;
import com.fintechnic.backend.model.User;


import lombok.extern.slf4j.Slf4j;


import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import com.fintechnic.backend.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/admin/user")
public class UserController {
    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    

    //Tìm kiếm và lấy danh sách người dùng
    @PostMapping
    public ResponseEntity<Page<UserListDTO>> getUsers(
            @RequestBody UserDTO request,
            @RequestParam(defaultValue = "0") int page,   // Phân trang: trang bắt đầu từ 0
            @RequestParam(defaultValue = "10") int size   // Số lượng kết quả mỗi trang
    ) {

        Page<UserListDTO> users = userService.getUsers(request, page, size);
        return ResponseEntity.ok(users);
    }


    //Xem chi tiết người dùng
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserDetails(@PathVariable Long userId) {
        User userDetails = userService.getUserDetails(userId);
        return ResponseEntity.ok(userDetails);
    }
    
    //Unlock người dùng
    @PostMapping("/unlock")
    public ResponseEntity<String> unlockUser(@RequestBody String username) {
        userService.unlockUser(username);
        return ResponseEntity.ok("User account has been unlocked.");
    }

    //Set role cho người dùng
    @PostMapping("/{userId}/update-role")
    public ResponseEntity<String> updateUserRole(@PathVariable Long userId, @RequestBody UserActionDTO request) {
        userService.setUserRole(userId, request.getNewRole());
        return ResponseEntity.ok("User role has been updated.");
    }

    //Reset mật khẩu người dùng
    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<String> resetUserPassword(@PathVariable Long userId, @RequestBody UserActionDTO request) {
    // Gọi service reset password và lấy thông báo thành công
    String message = userService.resetUserPassword(userId, request);
    return ResponseEntity.ok(message);
}    
}
```
controller/WalletAdminController.java:
package com.fintechnic.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintechnic.backend.dto.WalletSummaryDTO;
import com.fintechnic.backend.service.WalletService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/wallet")
public class WalletAdminController {
    private final WalletService walletService;

    @GetMapping("/summary")
    public ResponseEntity<WalletSummaryDTO> getWalletSummary() {
        return ResponseEntity.ok(walletService.getWalletSummary());
    }
}
```
dto/request/BillRequestDTO.java:
package com.fintechnic.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillRequestDTO {
    private String type;
    private String phoneNumber;
    private BigDecimal amount;
}
```
dto/request/QRCodeRequestDTO.java:
package com.fintechnic.backend.dto.request;

import lombok.Data;

@Data
public class QRCodeRequestDTO {
    private String encryptedData;
}
```
dto/request/TopUpRequestDTO.java:
package com.fintechnic.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopUpRequestDTO {

    @NotNull
    private String phoneNumber;

    @NotNull
    private BigDecimal amount; // Số tiền nạp

    private String description; // Mô tả giao dịch
}
```
dto/request/TransactionFilterRequestDTO.java:
package com.fintechnic.backend.dto.request;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fintechnic.backend.model.TransactionStatus;
import com.fintechnic.backend.model.TransactionType;

@Data
public class TransactionFilterRequestDTO {
    private String transactionCode;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String keyword;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Long fromWalletId;
    private Long toWalletId;

    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
    private int page = 0;
    private int size = 20;
}
```
dto/request/TransferRequestDTO.java:
package com.fintechnic.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {
    private BigDecimal amount;
    private String phoneNumber;
    private String description;
}
```
dto/request/WalletRequestDTO.java:
package com.fintechnic.backend.dto.request;

import lombok.Data;

@Data
public class WalletRequestDTO {
    private Long userId;  // ID của agent user (tùy chọn)
    private String username;   // Tìm kiếm theo username (tùy chọn)
    private String email;      // Tìm kiếm theo email (tùy chọn)
    private String phoneNumber; // Tìm kiếm theo số điện thoại (tùy chọn)
}
```
dto/request/WithdrawRequestDTO.java:
package com.fintechnic.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequestDTO {
    private BigDecimal amount;
}
```
dto/response/BillResponseDTO.java:
package com.fintechnic.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillResponseDTO {
    private String type;
    private String phoneNumber;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private Boolean isPaid;
}
```
dto/response/TopUpResponseDTO.java;
package com.fintechnic.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // chỉ trả về field nào có giá trị
public class TopUpResponseDTO {

    @NotNull
    private Long agentUserId;                // Bắt buộc khi request

    private String username;
    private BigDecimal newBalance;             
    private String transactionCode;            // Mã giao dịch trả về cho UI
    private BigDecimal amount;               // Số tiền nạp
    private String description;              // Mô tả giao dịch
    private String status;                   
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // Thời gian tạo
}
```
dto/response/TransactionFilterResponseDTO.java:
package com.fintechnic.backend.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fintechnic.backend.model.TransactionStatus;
import com.fintechnic.backend.model.TransactionType;

@Data
public class TransactionFilterResponseDTO {
    private String transactionCode;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
    private Long fromWalletId;
    private Long toWalletId;
}
```
dto/response/TransferResponseDTO.java:
package com.fintechnic.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TransferResponseDTO {
    private String counterparty; // bên còn lại (A đối với B, B đối với A)
    private BigDecimal amount;
    private String description;
    private String status;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private String type;
}
```
dto/response/WithdrawResponseDTO.java:
package com.fintechnic.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawResponseDTO {
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
}
package com.fintechnic.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawResponseDTO {
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
}
```
HomeDTO.java:
package com.fintechnic.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeDTO {
    private String username;
    private BigDecimal balance;
}
```
SystemStatsDTO.java:
package com.fintechnic.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemStatsDTO {
    private Long totalUsers;
    private Long totalTransactions;

}
```
UserActionDTO.java:
package com.fintechnic.backend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserActionDTO {
    private String newPassword;  // Mật khẩu mới (cho reset mật khẩu)
    private String newRole;      // Role mới (cho thay đổi quyền)
}
```
UserDTO.java:
package com.fintechnic.backend.dto;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
}
```
UserListDTO.java:
package com.fintechnic.backend.dto;

import java.time.LocalDateTime;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListDTO {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String role;
    private Boolean accountLocked;
    private int failedLoginAttempts;
    private LocalDateTime createdAt;

}
```
WalletSummaryDTO.java:
package com.fintechnic.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WalletSummaryDTO {
    private BigDecimal totalBalance;
    private BigDecimal averageBalance;

}
```
