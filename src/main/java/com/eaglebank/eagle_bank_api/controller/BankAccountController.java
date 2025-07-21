package com.eaglebank.eagle_bank_api.controller;

import java.util.List;

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

import com.eaglebank.eagle_bank_api.dto.BankAccountCreateRequest;
import com.eaglebank.eagle_bank_api.dto.BankAccountResponse;
import com.eaglebank.eagle_bank_api.dto.BankAccountUpdateRequest;
import com.eaglebank.eagle_bank_api.service.BankAccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/accounts")
public class BankAccountController {
    
    private final BankAccountService bankAccountService;
    
    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }
    
    @PostMapping
    public ResponseEntity<BankAccountResponse> createBankAccount(
            @RequestHeader("X-User-Id") Long authenticatedUserId,
            @Valid @RequestBody BankAccountCreateRequest request) {
        BankAccountResponse response = bankAccountService.createBankAccount(authenticatedUserId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<BankAccountResponse>> getUserBankAccounts(
            @RequestHeader("X-User-Id") Long authenticatedUserId) {
        List<BankAccountResponse> response = bankAccountService.getUserBankAccounts(authenticatedUserId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{accountId}")
    public ResponseEntity<BankAccountResponse> getBankAccountById(
            @PathVariable Long accountId,
            @RequestHeader("X-User-Id") Long authenticatedUserId) {
        BankAccountResponse response = bankAccountService.getBankAccountById(accountId, authenticatedUserId);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{accountId}")
    public ResponseEntity<BankAccountResponse> updateBankAccount(
            @PathVariable Long accountId,
            @RequestHeader("X-User-Id") Long authenticatedUserId,
            @Valid @RequestBody BankAccountUpdateRequest request) {
        BankAccountResponse response = bankAccountService.updateBankAccount(accountId, authenticatedUserId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteBankAccount(
            @PathVariable Long accountId,
            @RequestHeader("X-User-Id") Long authenticatedUserId) {
        bankAccountService.deleteBankAccount(accountId, authenticatedUserId);
        return ResponseEntity.noContent().build();
    }
}