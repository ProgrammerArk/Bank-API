package com.eaglebank.eagle_bank_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eaglebank.eagle_bank_api.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByBankAccountAccountIdOrderByTransactionDateDesc(Long accountId);
    
    List<Transaction> findByBankAccountUserUserIdOrderByTransactionDateDesc(Long userId);
}