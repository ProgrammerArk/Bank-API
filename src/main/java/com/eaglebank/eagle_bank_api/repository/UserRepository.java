package com.eaglebank.eagle_bank_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eaglebank.eagle_bank_api.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT CASE WHEN COUNT(ba) > 0 THEN true ELSE false END FROM BankAccount ba WHERE ba.user.userId = :userId")
    boolean hasBankAccounts(@Param("userId") Long userId);
}