package com.eaglebank.eagle_bank_api.dto;

import jakarta.validation.constraints.Size;

public class BankAccountUpdateRequest {
    @Size(min = 2, max = 100, message = "Account name must be between 2 and 100 characters")
    private String accountName;
    
    private String accountType;
    
    // Constructors
    public BankAccountUpdateRequest() {}
    
    // Getters and Setters
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
}