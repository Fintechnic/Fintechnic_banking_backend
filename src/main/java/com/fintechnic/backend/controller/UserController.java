package com.fintechnic.backend.controller;

import com.fintechnic.backend.dto.UserDTO;
import com.fintechnic.backend.service.UserService;
import com.fintechnic.backend.model.User;
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

    // Get all users 
    @GetMapping
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

    // Set user role (ADMIN/USER
    @PostMapping("/{userId}/role")
    public ResponseEntity<String> setUserRole(@PathVariable Long userId, @RequestParam String role) {
        userService.setUserRole(userId, role);
        return ResponseEntity.ok("User role updated to " + role);
    }

    // Find user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // Log out user (Remove all JWT tokens)
    @PostMapping("/{username}/logout")
    public ResponseEntity<String> logoutUser(@PathVariable String username) {
        userService.logoutUser(username);
        return ResponseEntity.ok("User " + username + " logged out successfully");
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.ok("User deleted successfully");
}

    // Update user in4
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
        @PathVariable Long id,
        @RequestBody UserDTO dto) {

    UserDTO updated = userService.updateUser(id, dto);
    return ResponseEntity.ok(updated);
    }



}
