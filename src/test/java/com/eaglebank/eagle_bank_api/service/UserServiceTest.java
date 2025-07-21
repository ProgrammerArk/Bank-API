package com.eaglebank.eagle_bank_api.service;


import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaglebank.eagle_bank_api.dto.UserCreateRequest;
import com.eaglebank.eagle_bank_api.dto.UserResponse;
import com.eaglebank.eagle_bank_api.dto.UserUpdateRequest;
import com.eaglebank.eagle_bank_api.entity.User;
import com.eaglebank.eagle_bank_api.exception.ConflictException;
import com.eaglebank.eagle_bank_api.exception.ForbiddenException;
import com.eaglebank.eagle_bank_api.exception.ResourceNotFoundException;
import com.eaglebank.eagle_bank_api.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserCreateRequest createRequest;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("1234567890");
        user.setAddress("123 Main St");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        createRequest = new UserCreateRequest();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setEmail("john.doe@example.com");
        createRequest.setPhoneNumber("1234567890");
        createRequest.setAddress("123 Main St");

        updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("Jane");
        updateRequest.setPhoneNumber("0987654321");
    }

    @Test
    void createUser_ValidRequest_ReturnsUserResponse() {
        // Given
        when(userRepository.existsByEmail(eq("john.doe@example.com"))).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserResponse response = userService.createUser(createRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe@example.com", response.getEmail());
        
        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsConflictException() {
        // Given
        when(userRepository.existsByEmail(eq("john.doe@example.com"))).thenReturn(true);

        // When & Then
        assertThrows(ConflictException.class, () -> userService.createUser(createRequest));
        
        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_ValidRequest_ReturnsUserResponse() {
        // Given
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));

        // When
        UserResponse response = userService.getUserById(1L, 1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("John", response.getFirstName());
        
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_DifferentUserId_ThrowsForbiddenException() {
        // When & Then
        assertThrows(ForbiddenException.class, () -> userService.getUserById(1L, 2L));
        
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getUserById_UserNotFound_ThrowsResourceNotFoundException() {
        // Given
        when(userRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L, 1L));
        
        verify(userRepository).findById(1L);
    }

    @Test
    void updateUser_ValidRequest_ReturnsUpdatedUserResponse() {
        // Given
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserResponse response = userService.updateUser(1L, 1L, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals("Jane", user.getFirstName()); // User object should be updated
        assertEquals("0987654321", user.getPhoneNumber());
        
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_DifferentUserId_ThrowsForbiddenException() {
        // When & Then
        assertThrows(ForbiddenException.class, () -> userService.updateUser(1L, 2L, updateRequest));
        
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_EmailAlreadyExists_ThrowsConflictException() {
        // Given
        updateRequest.setEmail("existing@example.com");
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(eq("existing@example.com"))).thenReturn(true);

        // When & Then
        assertThrows(ConflictException.class, () -> userService.updateUser(1L, 1L, updateRequest));
        
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_ValidRequest_DeletesUser() {
        // Given
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(userRepository.hasBankAccounts(eq(1L))).thenReturn(false);

        // When
        userService.deleteUser(1L, 1L);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).hasBankAccounts(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_UserHasBankAccounts_ThrowsConflictException() {
        // Given
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(userRepository.hasBankAccounts(eq(1L))).thenReturn(true);

        // When & Then
        assertThrows(ConflictException.class, () -> userService.deleteUser(1L, 1L));
        
        verify(userRepository).findById(1L);
        verify(userRepository).hasBankAccounts(1L);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteUser_DifferentUserId_ThrowsForbiddenException() {
        // When & Then
        assertThrows(ForbiddenException.class, () -> userService.deleteUser(1L, 2L));
        
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).delete(any());
    }
}