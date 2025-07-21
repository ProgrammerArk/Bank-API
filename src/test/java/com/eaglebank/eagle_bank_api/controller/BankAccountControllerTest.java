package com.eaglebank.eagle_bank_api.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

import com.eaglebank.eagle_bank_api.dto.BankAccountCreateRequest;
import com.eaglebank.eagle_bank_api.dto.BankAccountResponse;
import com.eaglebank.eagle_bank_api.dto.BankAccountUpdateRequest;
import com.eaglebank.eagle_bank_api.service.BankAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BankAccountController.class)
class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankAccountService bankAccountService;

    @Autowired
    private ObjectMapper objectMapper;

    private BankAccountCreateRequest createRequest;
    private BankAccountUpdateRequest updateRequest;
    private BankAccountResponse accountResponse;

    @BeforeEach
    void setUp() {
        createRequest = new BankAccountCreateRequest();
        createRequest.setAccountName("Savings Account");
        createRequest.setAccountType("SAVINGS");
        createRequest.setInitialBalance(new BigDecimal("1000.00"));

        updateRequest = new BankAccountUpdateRequest();
        updateRequest.setAccountName("Updated Savings Account");
        updateRequest.setAccountType("CHECKING");

        accountResponse = new BankAccountResponse(
            1L, "Savings Account", "SAVINGS", 
            new BigDecimal("1000.00"), "EB1705312345678", 1L,
            LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void createBankAccount_ValidRequest_ReturnsCreated() throws Exception {
        when(bankAccountService.createBankAccount(eq(1L), any(BankAccountCreateRequest.class)))
            .thenReturn(accountResponse);

        mockMvc.perform(post("/v1/accounts")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.accountName").value("Savings Account"))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.balance").value(1000.00))
                .andExpect(jsonPath("$.accountNumber").value("EB1705312345678"));
    }

    @Test
    void createBankAccount_InvalidRequest_ReturnsBadRequest() throws Exception {
        BankAccountCreateRequest invalidRequest = new BankAccountCreateRequest();
        // Missing required fields

        mockMvc.perform(post("/v1/accounts")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserBankAccounts_ValidRequest_ReturnsAccountList() throws Exception {
        BankAccountResponse account2 = new BankAccountResponse(
            2L, "Checking Account", "CHECKING", 
            new BigDecimal("500.00"), "EB1705312345679", 1L,
            LocalDateTime.now(), LocalDateTime.now()
        );
        
        List<BankAccountResponse> accounts = Arrays.asList(accountResponse, account2);
        when(bankAccountService.getUserBankAccounts(eq(1L))).thenReturn(accounts);

        mockMvc.perform(get("/v1/accounts")
                .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].accountId").value(1))
                .andExpect(jsonPath("$[1].accountId").value(2));
    }

    @Test
    void getBankAccountById_ValidRequest_ReturnsAccount() throws Exception {
        when(bankAccountService.getBankAccountById(eq(1L), eq(1L))).thenReturn(accountResponse);

        mockMvc.perform(get("/v1/accounts/1")
                .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.accountName").value("Savings Account"));
    }

    @Test
    void updateBankAccount_ValidRequest_ReturnsUpdatedAccount() throws Exception {
        BankAccountResponse updatedResponse = new BankAccountResponse(
            1L, "Updated Savings Account", "CHECKING", 
            new BigDecimal("1000.00"), "EB1705312345678", 1L,
            LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(bankAccountService.updateBankAccount(eq(1L), eq(1L), any(BankAccountUpdateRequest.class)))
            .thenReturn(updatedResponse);

        mockMvc.perform(patch("/v1/accounts/1")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountName").value("Updated Savings Account"))
                .andExpect(jsonPath("$.accountType").value("CHECKING"));
    }

    @Test
    void deleteBankAccount_ValidRequest_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/v1/accounts/1")
                .header("X-User-Id", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createBankAccount_MissingAuthHeader_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());
    }
}