package com.fintechnic.backend.controller;

import com.fintechnic.backend.dto.UserDTO;
import com.fintechnic.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get all users (for admin dashboard)
    @GetMapping("/getall")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUserDTOs();
        return ResponseEntity.ok(users);
    }

    // Unlock a user account
    @PostMapping("/{username}/unlock")
    public ResponseEntity<String> unlockUser(@PathVariable String username) {
        userService.unlockUser(username);
        return ResponseEntity.ok("User " + username + " unlocked successfully");
    }

    // Set user role (Admin/User)
    @PostMapping("/{userId}/role")
    public ResponseEntity<String> setUserRole(@PathVariable Long userId, @RequestParam String role) {
        userService.setUserRole(userId, role);
        return ResponseEntity.ok("User role updated to " + role);
    }

    // Logout a user (remove all JWT tokens)
    @PostMapping("/{username}/logout")
    public ResponseEntity<String> logoutUser(@PathVariable String username) {
        userService.logoutUser(username);
        return ResponseEntity.ok("User " + username + " logged out successfully");
    }
}
