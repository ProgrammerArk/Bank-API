package com.eaglebank.eagle_bank_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglebank.eagle_bank_api.dto.BankAccountCreateRequest;
import com.eaglebank.eagle_bank_api.dto.BankAccountResponse;
import com.eaglebank.eagle_bank_api.dto.BankAccountUpdateRequest;
import com.eaglebank.eagle_bank_api.entity.BankAccount;
import com.eaglebank.eagle_bank_api.entity.User;
import com.eaglebank.eagle_bank_api.exception.ForbiddenException;
import com.eaglebank.eagle_bank_api.exception.ResourceNotFoundException;
import com.eaglebank.eagle_bank_api.repository.BankAccountRepository;
import com.eaglebank.eagle_bank_api.repository.UserRepository;

@Service
@Transactional
public class BankAccountService {
    
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }
    
    public BankAccountResponse createBankAccount(Long authenticatedUserId, BankAccountCreateRequest request) {
        User user = userRepository.findById(authenticatedUserId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + authenticatedUserId));
        
        BankAccount bankAccount = new BankAccount(
            request.getAccountName(),
            request.getAccountType(),
            request.getInitialBalance(),
            user
        );
        
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);
        return convertToBankAccountResponse(savedAccount);
    }
    
    public List<BankAccountResponse> getUserBankAccounts(Long authenticatedUserId) {
        List<BankAccount> accounts = bankAccountRepository.findByUserUserId(authenticatedUserId);
        return accounts.stream()
            .map(this::convertToBankAccountResponse)
            .collect(Collectors.toList());
    }
    
    public BankAccountResponse getBankAccountById(Long accountId, Long authenticatedUserId) {
        BankAccount account = bankAccountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Bank account not found with ID: " + accountId));
        
        // Check if account belongs to authenticated user
        if (!account.getUser().getUserId().equals(authenticatedUserId)) {
            throw new ForbiddenException("You can only access your own bank accounts");
        }
        
        return convertToBankAccountResponse(account);
    }
    
    public BankAccountResponse updateBankAccount(Long accountId, Long authenticatedUserId, BankAccountUpdateRequest request) {
        BankAccount account = bankAccountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Bank account not found with ID: " + accountId));
        
        // Check if account belongs to authenticated user
        if (!account.getUser().getUserId().equals(authenticatedUserId)) {
            throw new ForbiddenException("You can only update your own bank accounts");
        }
        
        // Update only non-null fields
        if (request.getAccountName() != null) {
            account.setAccountName(request.getAccountName());
        }
        if (request.getAccountType() != null) {
            account.setAccountType(request.getAccountType());
        }
        
        BankAccount updatedAccount = bankAccountRepository.save(account);
        return convertToBankAccountResponse(updatedAccount);
    }
    
    public void deleteBankAccount(Long accountId, Long authenticatedUserId) {
        BankAccount account = bankAccountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Bank account not found with ID: " + accountId));
        
        // Check if account belongs to authenticated user
        if (!account.getUser().getUserId().equals(authenticatedUserId)) {
            throw new ForbiddenException("You can only delete your own bank accounts");
        }
        
        bankAccountRepository.delete(account);
    }
    
    public BankAccount findByIdAndValidateOwnership(Long accountId, Long authenticatedUserId) {
        BankAccount account = bankAccountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Bank account not found with ID: " + accountId));
        
        if (!account.getUser().getUserId().equals(authenticatedUserId)) {
            throw new ForbiddenException("You can only access your own bank accounts");
        }
        
        return account;
    }
    
    private BankAccountResponse convertToBankAccountResponse(BankAccount account) {
        return new BankAccountResponse(
            account.getAccountId(),
            account.getAccountName(),
            account.getAccountType(),
            account.getBalance(),
            account.getAccountNumber(),
            account.getUser().getUserId(),
            account.getCreatedAt(),
            account.getUpdatedAt()
        );
    }
}