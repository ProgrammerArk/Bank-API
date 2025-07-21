package com.eaglebank.eagle_bank_api.controller;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eaglebank.eagle_bank_api.dto.TransactionCreateRequest;
import com.eaglebank.eagle_bank_api.dto.TransactionResponse;
import com.eaglebank.eagle_bank_api.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    // Helper methods to create test data - more flexible than @BeforeEach
    private TransactionCreateRequest createDepositRequest() {
        TransactionCreateRequest request = new TransactionCreateRequest();
        request.setAmount(new BigDecimal("500.00"));
        request.setTransactionType("DEPOSIT");
        request.setDescription("Salary deposit");
        return request;
    }

    private TransactionCreateRequest createWithdrawalRequest() {
        TransactionCreateRequest request = new TransactionCreateRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setTransactionType("WITHDRAWAL");
        request.setDescription("ATM withdrawal");
        return request;
    }

    private TransactionResponse createTransactionResponse() {
        return new TransactionResponse(
            1L, new BigDecimal("500.00"), "DEPOSIT", 
            "Salary deposit", new BigDecimal("1500.00"), 
            LocalDateTime.now(), 1L
        );
    }

    @Test
    void createTransaction_Deposit_ReturnsCreated() throws Exception {
        TransactionResponse response = createTransactionResponse();
        when(transactionService.createTransaction(eq(1L), eq(1L), any(TransactionCreateRequest.class)))
            .thenReturn(response);

        mockMvc.perform(post("/v1/accounts/1/transactions")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDepositRequest())))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactionId").value(1))
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$.balanceAfter").value(1500.00))
                .andExpect(jsonPath("$.accountId").value(1));
    }

    @Test
    void createTransaction_Withdrawal_ReturnsCreated() throws Exception {
        TransactionResponse withdrawalResponse = new TransactionResponse(
            2L, new BigDecimal("100.00"), "WITHDRAWAL", 
            "ATM withdrawal", new BigDecimal("1400.00"), 
            LocalDateTime.now(), 1L
        );
        
        when(transactionService.createTransaction(eq(1L), eq(1L), any(TransactionCreateRequest.class)))
            .thenReturn(withdrawalResponse);

        mockMvc.perform(post("/v1/accounts/1/transactions")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createWithdrawalRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionType").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.balanceAfter").value(1400.00));
    }

    @Test
    void createTransaction_InvalidRequest_ReturnsBadRequest() throws Exception {
        TransactionCreateRequest invalidRequest = new TransactionCreateRequest();
        // Missing required fields

        mockMvc.perform(post("/v1/accounts/1/transactions")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTransaction_InvalidTransactionType_ReturnsBadRequest() throws Exception {
        TransactionCreateRequest invalidRequest = new TransactionCreateRequest();
        invalidRequest.setAmount(new BigDecimal("100.00"));
        invalidRequest.setTransactionType("INVALID_TYPE");
        invalidRequest.setDescription("Test");

        mockMvc.perform(post("/v1/accounts/1/transactions")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAccountTransactions_ValidRequest_ReturnsTransactionList() throws Exception {
        TransactionResponse transaction2 = new TransactionResponse(
            2L, new BigDecimal("100.00"), "WITHDRAWAL", 
            "ATM withdrawal", new BigDecimal("1400.00"), 
            LocalDateTime.now(), 1L
        );
        
        List<TransactionResponse> transactions = Arrays.asList(transaction2, createTransactionResponse());
        when(transactionService.getAccountTransactions(eq(1L), eq(1L))).thenReturn(transactions);

        mockMvc.perform(get("/v1/accounts/1/transactions")
                .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].transactionId").value(2))
                .andExpect(jsonPath("$[1].transactionId").value(1));
    }

    @Test
    void getUserTransactions_ValidRequest_ReturnsTransactionList() throws Exception {
        List<TransactionResponse> transactions = Arrays.asList(createTransactionResponse());
        when(transactionService.getUserTransactions(eq(1L))).thenReturn(transactions);

        mockMvc.perform(get("/v1/accounts/transactions")
                .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].transactionId").value(1))
                .andExpect(jsonPath("$[0].transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$[0].amount").value(500.00));
    }

    @Test
    void createTransaction_MissingAuthHeader_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/v1/accounts/1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDepositRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createTransaction_NegativeAmount_ReturnsBadRequest() throws Exception {
        TransactionCreateRequest invalidRequest = new TransactionCreateRequest();
        invalidRequest.setAmount(new BigDecimal("-100.00"));
        invalidRequest.setTransactionType("DEPOSIT");
        invalidRequest.setDescription("Invalid amount");

        mockMvc.perform(post("/v1/accounts/1/transactions")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}