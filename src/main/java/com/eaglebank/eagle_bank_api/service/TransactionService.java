package com.eaglebank.eagle_bank_api.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglebank.eagle_bank_api.dto.TransactionCreateRequest;
import com.eaglebank.eagle_bank_api.dto.TransactionResponse;
import com.eaglebank.eagle_bank_api.entity.BankAccount;
import com.eaglebank.eagle_bank_api.entity.Transaction;
import com.eaglebank.eagle_bank_api.enums.TransactionType;
import com.eaglebank.eagle_bank_api.exception.UnprocessableEntityException;
import com.eaglebank.eagle_bank_api.repository.BankAccountRepository;
import com.eaglebank.eagle_bank_api.repository.TransactionRepository;

@Service
@Transactional
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountService bankAccountService;
    
    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                             BankAccountRepository bankAccountRepository,
                             BankAccountService bankAccountService) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountService = bankAccountService;
    }
    
    public TransactionResponse createTransaction(Long accountId, Long authenticatedUserId, TransactionCreateRequest request) {
        // Validate account ownership
        BankAccount account = bankAccountService.findByIdAndValidateOwnership(accountId, authenticatedUserId);
        
        TransactionType transactionType = TransactionType.fromValue(request.getTransactionType());
        BigDecimal amount = request.getAmount();
        BigDecimal currentBalance = account.getBalance();
        BigDecimal newBalance;
        
        // Calculate new balance based on transaction type
        if (transactionType == TransactionType.DEPOSIT) {
            newBalance = currentBalance.add(amount);
        } else { // WITHDRAWAL
            if (currentBalance.compareTo(amount) < 0) {
                throw new UnprocessableEntityException("Insufficient funds. Current balance: " + currentBalance);
            }
            newBalance = currentBalance.subtract(amount);
        }
        
        // Create transaction
        Transaction transaction = new Transaction(
            amount,
            transactionType.getValue(),
            request.getDescription(),
            newBalance,
            account
        );
        
        // Update account balance
        account.setBalance(newBalance);
        bankAccountRepository.save(account);
        
        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return convertToTransactionResponse(savedTransaction);
    }
    
    public List<TransactionResponse> getAccountTransactions(Long accountId, Long authenticatedUserId) {
        // Validate account ownership
        bankAccountService.findByIdAndValidateOwnership(accountId, authenticatedUserId);
        
        List<Transaction> transactions = transactionRepository.findByBankAccountAccountIdOrderByTransactionDateDesc(accountId);
        return transactions.stream()
            .map(this::convertToTransactionResponse)
            .collect(Collectors.toList());
    }
    
    public List<TransactionResponse> getUserTransactions(Long authenticatedUserId) {
        List<Transaction> transactions = transactionRepository.findByBankAccountUserUserIdOrderByTransactionDateDesc(authenticatedUserId);
        return transactions.stream()
            .map(this::convertToTransactionResponse)
            .collect(Collectors.toList());
    }
    
    private TransactionResponse convertToTransactionResponse(Transaction transaction) {
        return new TransactionResponse(
            transaction.getTransactionId(),
            transaction.getAmount(),
            transaction.getTransactionType(),
            transaction.getDescription(),
            transaction.getBalanceAfter(),
            transaction.getTransactionDate(),
            transaction.getBankAccount().getAccountId()
        );
    }
}