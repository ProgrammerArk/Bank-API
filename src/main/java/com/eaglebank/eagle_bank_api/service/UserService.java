package com.eaglebank.eagle_bank_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglebank.eagle_bank_api.dto.UserCreateRequest;
import com.eaglebank.eagle_bank_api.dto.UserResponse;
import com.eaglebank.eagle_bank_api.dto.UserUpdateRequest;
import com.eaglebank.eagle_bank_api.entity.User;
import com.eaglebank.eagle_bank_api.exception.ConflictException;
import com.eaglebank.eagle_bank_api.exception.ForbiddenException;
import com.eaglebank.eagle_bank_api.exception.ResourceNotFoundException;
import com.eaglebank.eagle_bank_api.repository.UserRepository;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public UserResponse createUser(UserCreateRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        
        User user = new User(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPhoneNumber(),
            request.getAddress()
        );
        
        User savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }
    
    public UserResponse getUserById(Long userId, Long authenticatedUserId) {
        // Check if user is trying to access their own details
        if (!userId.equals(authenticatedUserId)) {
            throw new ForbiddenException("You can only access your own user details");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        return convertToUserResponse(user);
    }
    
    public UserResponse updateUser(Long userId, Long authenticatedUserId, UserUpdateRequest request) {
        // Check if user is trying to update their own details
        if (!userId.equals(authenticatedUserId)) {
            throw new ForbiddenException("You can only update your own user details");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Update only non-null fields
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            // Check if new email already exists (excluding current user)
            if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        
        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }
    
    public void deleteUser(Long userId, Long authenticatedUserId) {
        // Check if user is trying to delete their own account
        if (!userId.equals(authenticatedUserId)) {
            throw new ForbiddenException("You can only delete your own user account");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Check if user has bank accounts
        if (userRepository.hasBankAccounts(userId)) {
            throw new ConflictException("Cannot delete user with existing bank accounts");
        }
        
        userRepository.delete(user);
    }
    
    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
            user.getUserId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getAddress(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}