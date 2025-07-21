package com.eaglebank.eagle_bank_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankAccountResponse {
    private Long accountId;
    private String accountName;
    private String accountType;
    private BigDecimal balance;
    private String accountNumber;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public BankAccountResponse() {}
    
    public BankAccountResponse(Long accountId, String accountName, String accountType, 
                              BigDecimal balance, String accountNumber, Long userId,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountType = accountType;
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}