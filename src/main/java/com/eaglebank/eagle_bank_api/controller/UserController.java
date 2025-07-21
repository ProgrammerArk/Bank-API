package com.eaglebank.eagle_bank_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eaglebank.eagle_bank_api.dto.UserCreateRequest;
import com.eaglebank.eagle_bank_api.dto.UserResponse;
import com.eaglebank.eagle_bank_api.dto.UserUpdateRequest;
import com.eaglebank.eagle_bank_api.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId,
                                                   @RequestHeader("X-User-Id") Long authenticatedUserId) {
        UserResponse response = userService.getUserById(userId, authenticatedUserId);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId,
                                                  @RequestHeader("X-User-Id") Long authenticatedUserId,
                                                  @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUser(userId, authenticatedUserId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId,
                                          @RequestHeader("X-User-Id") Long authenticatedUserId) {
        userService.deleteUser(userId, authenticatedUserId);
        return ResponseEntity.noContent().build();
    }
}