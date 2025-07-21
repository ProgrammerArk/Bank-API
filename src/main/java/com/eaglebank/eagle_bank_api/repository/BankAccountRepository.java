package com.eaglebank.eagle_bank_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eaglebank.eagle_bank_api.entity.BankAccount;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    
    List<BankAccount> findByUserUserId(Long userId);
    
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    
    boolean existsByAccountNumber(String accountNumber);
}
