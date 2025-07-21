# Testing Guide for Eagle Bank API

This document explains how to run and understand the comprehensive test suite for the Eagle Bank API.

## Test Structure

The test suite includes:

### **Unit Tests**
- **UserControllerTest** - Tests user endpoints with mocked services
- **BankAccountControllerTest** - Tests bank account endpoints  
- **TransactionControllerTest** - Tests transaction endpoints
- **UserServiceTest** - Tests user business logic
- **TransactionServiceTest** - Tests transaction business logic

### **Integration Tests**
- **EagleBankIntegrationTest** - End-to-end testing with real database

## Running Tests

### **Run All Tests**
```bash
mvn test
```

### **Run Tests with Coverage**
```bash
mvn test jacoco:report
```

## Test Coverage

### **UserControllerTest**
Covers all 30 scenarios:
- User creation with valid data
- User creation with missing data (400 Bad Request)
- User retrieval with authentication
- Forbidden access to other users (403 Forbidden)  
- User not found (404 Not Found)
- User updates with validation
- User deletion scenarios

### **BankAccountControllerTest**
**Covers account management:**
- Account creation and validation
- Retrieving user accounts
- Account updates and deletion
- Authorization checks
- Missing authentication handling

### **TransactionControllerTest**
**Covers transaction scenarios:**
- Deposits and withdrawals
- Invalid transaction types
- Negative amounts
- Transaction history retrieval
- Authorization validation

### **Service Layer Tests**
**Business logic validation:**
- Email uniqueness checks
- Insufficient funds handling
- User-account ownership validation
- Transaction balance calculations

### **Integration Tests**
**End-to-end scenarios:**
- Complete user journey (register → create account → transactions)
- Security enforcement across endpoints
- Business rule validation
- Error handling scenarios
