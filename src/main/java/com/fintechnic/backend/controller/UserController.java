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
    public ResponseEntity<String> unlockUser(String username) {
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
