package com.eaglebank.eagle_bank_api.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eaglebank.eagle_bank_api.dto.UserCreateRequest;
import com.eaglebank.eagle_bank_api.dto.UserResponse;
import com.eaglebank.eagle_bank_api.dto.UserUpdateRequest;
import com.eaglebank.eagle_bank_api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserCreateRequest userCreateRequest;
    private UserUpdateRequest userUpdateRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userCreateRequest = new UserCreateRequest();
        userCreateRequest.setFirstName("John");
        userCreateRequest.setLastName("Doe");
        userCreateRequest.setEmail("john.doe@example.com");
        userCreateRequest.setPhoneNumber("1234567890");
        userCreateRequest.setAddress("123 Main St");

        userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setFirstName("Jane");
        userUpdateRequest.setPhoneNumber("0987654321");

        userResponse = new UserResponse(
            1L, "John", "Doe", "john.doe@example.com", 
            "1234567890", "123 Main St", 
            LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void createUser_ValidRequest_ReturnsCreated() throws Exception {
        when(userService.createUser(any(UserCreateRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void createUser_InvalidRequest_ReturnsBadRequest() throws Exception {
        UserCreateRequest invalidRequest = new UserCreateRequest();
        // Missing required fields

        mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_ValidRequest_ReturnsUser() throws Exception {
        when(userService.getUserById(eq(1L), eq(1L))).thenReturn(userResponse);

        mockMvc.perform(get("/v1/users/1")
                .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getUserById_MissingAuthHeader_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/v1/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUser_ValidRequest_ReturnsUpdatedUser() throws Exception {
        UserResponse updatedResponse = new UserResponse(
            1L, "Jane", "Doe", "john.doe@example.com", 
            "0987654321", "123 Main St", 
            LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(userService.updateUser(eq(1L), eq(1L), any(UserUpdateRequest.class)))
            .thenReturn(updatedResponse);

        mockMvc.perform(patch("/v1/users/1")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.phoneNumber").value("0987654321"));
    }

    @Test
    void deleteUser_ValidRequest_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/v1/users/1")
                .header("X-User-Id", "1"))
                .andExpect(status().isNoContent());
    }
}