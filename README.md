# Bank REST API
A comprehensive REST API for a Bank that allows users to manage their personal details, bank accounts, and perform financial transactions.

## Features
- **User Management**: Create, read, update, and delete user profiles
- **Bank Account Management**: Create, read, update, and delete bank accounts
- **Transaction Management**: Deposit and withdraw money with transaction history
- **Security**: Simple authentication using X-User-Id header
- **Validation**: Comprehensive input validation and error handling

## Technology Stack Used
- Java 17
- Spring Boot 3.5.3
- Spring Data JPA
- H2 Database (in-memory for development)
- Maven (build tool)

All endpoints except user registration require the X-User-Id header for authentication.

## Creating a New User
**URL**: *POST* http://localhost:8080/api/v1/users

```JSON
{
  "firstName": "John",
  "lastName": "Doe", 
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890",
  "address": "123 Main St, Anytown, USA"
}
```

## Getting User Details
**URL**: *GET* http://localhost:8080/api/v1/users/{userId}
**Postman Headers**: Key: X-User-Id     Value: {userID}

## Making an Account
**URL**: *POST* http://localhost:8080/api/v1/accounts
**Postman Headers**: Key: X-User-Id     Value: {userID}
```JSON
{
  "accountName": "My Savings Account",
  "accountType": "SAVINGS",
  "initialBalance": 1000.00
}
```

## Getting all Accounts per user
**URL**: *GET* http://localhost:8080/api/v1/accounts
**Postman Headers**: Key: X-User-Id     Value: {userID}


## Updating a Bank Account
**URL**: *PATCH* http://localhost:8080/api/v1/accounts/{accountID}
**Postman Headers**: Key: X-User-Id     Value: {userID}

```JSON
{
  "accountName": "Updated Account Name",
  "accountType": "CHECKING"
}
```
## Deleting a Bank Account
**URL**: *DELETE* http://localhost:8080/api/v1/accounts/{accountID}
**Postman Headers**: Key: X-User-Id     Value: {userID}


## Making a New Deposit
**URL**: *POST* http://localhost:8080/api/v1/accounts/{accountID}/transactions
**Postman Headers**: Key: X-User-Id     Value: {userID}

```JSON
{
  "amount": 500.00,
  "transactionType": "DEPOSIT",
  "description": "Monthly salary deposit"
}
```

## Making a New Withdrawal
**URL**: *POST* http://localhost:8080/api/v1/accounts/{accountID}/transactions
**Postman Headers**: Key: X-User-Id     Value: {userID}

```JSON
{
  "amount": 500.00,
  "transactionType": "WITHDRAWAL",
  "description": "ATM Withdrawal"
}
```

If there is an attempt to withdraw more money than available, an error message is shown

```JSON
{
    "message": "Insufficient funds. Current balance: 500.00",
    "status": 422,
    "timestamp": "2025-07-21T21:30:42.4494405"
}
```

## Checking all transactions
**URL**: *DELETE* http://localhost:8080/api/v1/accounts/transactions
**Postman Headers**: Key: X-User-Id     Value: {userID}

## Security and Validation
Added security and validation so that people who don't own the bank account (don't have a matching X-User-Id and userID) are unable to read update or modify any details of a different account. This is true for creating new bank account, modifying details, or deleting data and information.