package com.eaglebank.eagle_bank_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eaglebank.eagle_bank_api.dto.TransactionCreateRequest;
import com.eaglebank.eagle_bank_api.dto.TransactionResponse;
import com.eaglebank.eagle_bank_api.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/accounts")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<TransactionResponse> createTransaction(
            @PathVariable Long accountId,
            @RequestHeader("X-User-Id") Long authenticatedUserId,
            @Valid @RequestBody TransactionCreateRequest request) {
        TransactionResponse response = transactionService.createTransaction(accountId, authenticatedUserId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getAccountTransactions(
            @PathVariable Long accountId,
            @RequestHeader("X-User-Id") Long authenticatedUserId) {
        List<TransactionResponse> response = transactionService.getAccountTransactions(accountId, authenticatedUserId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getUserTransactions(
            @RequestHeader("X-User-Id") Long authenticatedUserId) {
        List<TransactionResponse> response = transactionService.getUserTransactions(authenticatedUserId);
        return ResponseEntity.ok(response);
    }
}