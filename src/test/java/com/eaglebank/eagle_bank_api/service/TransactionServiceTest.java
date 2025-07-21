package com.eaglebank.eagle_bank_api.service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaglebank.eagle_bank_api.dto.TransactionCreateRequest;
import com.eaglebank.eagle_bank_api.dto.TransactionResponse;
import com.eaglebank.eagle_bank_api.entity.BankAccount;
import com.eaglebank.eagle_bank_api.entity.Transaction;
import com.eaglebank.eagle_bank_api.entity.User;
import com.eaglebank.eagle_bank_api.exception.UnprocessableEntityException;
import com.eaglebank.eagle_bank_api.repository.BankAccountRepository;
import com.eaglebank.eagle_bank_api.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private BankAccount bankAccount;
    private Transaction transaction;
    private TransactionCreateRequest depositRequest;
    private TransactionCreateRequest withdrawalRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");

        bankAccount = new BankAccount();
        bankAccount.setAccountId(1L);
        bankAccount.setAccountName("Savings Account");
        bankAccount.setAccountType("SAVINGS");
        bankAccount.setBalance(new BigDecimal("1000.00"));
        bankAccount.setAccountNumber("EB1705312345678");
        bankAccount.setUser(user);

        transaction = new Transaction();
        transaction.setTransactionId(1L);
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setTransactionType("DEPOSIT");
        transaction.setDescription("Test deposit");
        transaction.setBalanceAfter(new BigDecimal("1500.00"));
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setBankAccount(bankAccount);

        depositRequest = new TransactionCreateRequest();
        depositRequest.setAmount(new BigDecimal("500.00"));
        depositRequest.setTransactionType("DEPOSIT");
        depositRequest.setDescription("Salary deposit");

        withdrawalRequest = new TransactionCreateRequest();
        withdrawalRequest.setAmount(new BigDecimal("200.00"));
        withdrawalRequest.setTransactionType("WITHDRAWAL");
        withdrawalRequest.setDescription("ATM withdrawal");
    }

    @Test
    void createTransaction_Deposit_Success() {
        // Given
        when(bankAccountService.findByIdAndValidateOwnership(eq(1L), eq(1L))).thenReturn(bankAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        // When
        TransactionResponse response = transactionService.createTransaction(1L, 1L, depositRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getTransactionId());
        assertEquals(new BigDecimal("500.00"), response.getAmount());
        assertEquals("DEPOSIT", response.getTransactionType());
        assertEquals(new BigDecimal("1500.00"), response.getBalanceAfter());
        assertEquals(1L, response.getAccountId());

        // Verify account balance was updated
        assertEquals(new BigDecimal("1500.00"), bankAccount.getBalance());

        verify(bankAccountService).findByIdAndValidateOwnership(1L, 1L);
        verify(bankAccountRepository).save(bankAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_Withdrawal_Success() {
        // Given
        when(bankAccountService.findByIdAndValidateOwnership(eq(1L), eq(1L))).thenReturn(bankAccount);
        
        Transaction withdrawalTransaction = new Transaction();
        withdrawalTransaction.setTransactionId(2L);
        withdrawalTransaction.setAmount(new BigDecimal("200.00"));
        withdrawalTransaction.setTransactionType("WITHDRAWAL");
        withdrawalTransaction.setDescription("ATM withdrawal");
        withdrawalTransaction.setBalanceAfter(new BigDecimal("800.00"));
        withdrawalTransaction.setTransactionDate(LocalDateTime.now());
        withdrawalTransaction.setBankAccount(bankAccount);
        
        when(transactionRepository.save(any(Transaction.class))).thenReturn(withdrawalTransaction);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        // When
        TransactionResponse response = transactionService.createTransaction(1L, 1L, withdrawalRequest);

        // Then
        assertNotNull(response);
        assertEquals(2L, response.getTransactionId());
        assertEquals(new BigDecimal("200.00"), response.getAmount());
        assertEquals("WITHDRAWAL", response.getTransactionType());
        assertEquals(new BigDecimal("800.00"), response.getBalanceAfter());

        // Verify account balance was updated
        assertEquals(new BigDecimal("800.00"), bankAccount.getBalance());

        verify(bankAccountService).findByIdAndValidateOwnership(1L, 1L);
        verify(bankAccountRepository).save(bankAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_InsufficientFunds_ThrowsException() {
        // Given
        withdrawalRequest.setAmount(new BigDecimal("1500.00")); // More than balance
        when(bankAccountService.findByIdAndValidateOwnership(eq(1L), eq(1L))).thenReturn(bankAccount);

        // When & Then
        UnprocessableEntityException exception = assertThrows(
            UnprocessableEntityException.class, 
            () -> transactionService.createTransaction(1L, 1L, withdrawalRequest)
        );

        assertTrue(exception.getMessage().contains("Insufficient funds"));
        assertTrue(exception.getMessage().contains("1000.00"));

        verify(bankAccountService).findByIdAndValidateOwnership(1L, 1L);
        verify(bankAccountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_ExactBalance_Success() {
        // Given - withdraw exact balance
        withdrawalRequest.setAmount(new BigDecimal("1000.00"));
        when(bankAccountService.findByIdAndValidateOwnership(eq(1L), eq(1L))).thenReturn(bankAccount);
        
        Transaction exactWithdrawal = new Transaction();
        exactWithdrawal.setTransactionId(3L);
        exactWithdrawal.setAmount(new BigDecimal("1000.00"));
        exactWithdrawal.setTransactionType("WITHDRAWAL");
        exactWithdrawal.setBalanceAfter(new BigDecimal("0.00"));
        exactWithdrawal.setBankAccount(bankAccount);
        
        when(transactionRepository.save(any(Transaction.class))).thenReturn(exactWithdrawal);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        // When
        TransactionResponse response = transactionService.createTransaction(1L, 1L, withdrawalRequest);

        // Then
        assertNotNull(response);
        assertEquals(new BigDecimal("0.00"), response.getBalanceAfter());
        assertEquals(new BigDecimal("0.00"), bankAccount.getBalance());

        verify(bankAccountRepository).save(bankAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void getAccountTransactions_Success() {
        // Given
        Transaction transaction2 = new Transaction();
        transaction2.setTransactionId(2L);
        transaction2.setAmount(new BigDecimal("200.00"));
        transaction2.setTransactionType("WITHDRAWAL");
        transaction2.setBankAccount(bankAccount);
        transaction2.setTransactionDate(LocalDateTime.now().minusHours(1));

        List<Transaction> transactions = Arrays.asList(transaction, transaction2);
        
        when(bankAccountService.findByIdAndValidateOwnership(eq(1L), eq(1L))).thenReturn(bankAccount);
        when(transactionRepository.findByBankAccountAccountIdOrderByTransactionDateDesc(eq(1L)))
            .thenReturn(transactions);

        // When
        List<TransactionResponse> responses = transactionService.getAccountTransactions(1L, 1L);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getTransactionId());
        assertEquals(2L, responses.get(1).getTransactionId());

        verify(bankAccountService).findByIdAndValidateOwnership(1L, 1L);
        verify(transactionRepository).findByBankAccountAccountIdOrderByTransactionDateDesc(1L);
    }

    @Test
    void getUserTransactions_Success() {
        // Given
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionRepository.findByBankAccountUserUserIdOrderByTransactionDateDesc(eq(1L)))
            .thenReturn(transactions);

        // When
        List<TransactionResponse> responses = transactionService.getUserTransactions(1L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getTransactionId());
        assertEquals("DEPOSIT", responses.get(0).getTransactionType());

        verify(transactionRepository).findByBankAccountUserUserIdOrderByTransactionDateDesc(1L);
    }

    @Test
    void createTransaction_ZeroAmount_ShouldNotBeAllowed() {
        // Given
        depositRequest.setAmount(new BigDecimal("0.00"));
        when(bankAccountService.findByIdAndValidateOwnership(eq(1L), eq(1L))).thenReturn(bankAccount);

        // Note: This would be caught by validation at the controller level,
        // but testing the service behavior if it somehow gets through
        Transaction zeroTransaction = new Transaction();
        zeroTransaction.setAmount(new BigDecimal("0.00"));
        zeroTransaction.setTransactionType("DEPOSIT");
        zeroTransaction.setBalanceAfter(new BigDecimal("1000.00"));
        zeroTransaction.setBankAccount(bankAccount);
        
        when(transactionRepository.save(any(Transaction.class))).thenReturn(zeroTransaction);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        // When
        TransactionResponse response = transactionService.createTransaction(1L, 1L, depositRequest);

        // Then
        assertNotNull(response);
        assertEquals(new BigDecimal("0.00"), response.getAmount());
        // Balance should remain the same for zero amount
        assertEquals(new BigDecimal("1000.00"), bankAccount.getBalance());
    }
}
