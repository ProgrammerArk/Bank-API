package com.eaglebank.eagle_bank_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {
    private Long transactionId;
    private BigDecimal amount;
    private String transactionType;
    private String description;
    private BigDecimal balanceAfter;
    private LocalDateTime transactionDate;
    private Long accountId;
    
    // Constructors
    public TransactionResponse() {}
    
    public TransactionResponse(Long transactionId, BigDecimal amount, String transactionType,
                              String description, BigDecimal balanceAfter, 
                              LocalDateTime transactionDate, Long accountId) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.balanceAfter = balanceAfter;
        this.transactionDate = transactionDate;
        this.accountId = accountId;
    }
    
    // Getters and Setters
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
}