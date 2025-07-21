# Bank REST API
A comprehensive REST API for a Bank that allows users to manage their personal details, bank accounts, and perform financial transactions.

## Features
User Management: Create, read, update, and delete user profiles
Bank Account Management: Create, read, update, and delete bank accounts
Transaction Management: Deposit and withdraw money with transaction history
Security: Simple authentication using X-User-Id header
Validation: Comprehensive input validation and error handling

## Technology Stack Used

Java 17
Spring Boot 3.5.3
Spring Data JPA
H2 Database (in-memory for development)
Maven (build tool)

All endpoints except user registration require the X-User-Id header for authentication.

## Creating a New User

URL: http://localhost:8080/api/v1/users
Content-Type: application/json

Body (Raw JSON)
{
  "firstName": "John",
  "lastName": "Doe", 
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890",
  "address": "123 Main St, Anytown, USA"
}

## Getting User Details

URL: http://localhost:8080/api/v1/users/{userId}

Postman Headers: Key: X-User-Id     Value: {userID}

## Security and Validation

Added security and validation so that people who don't own the bank account (don't have a matching X-User-Id and userID) are unable to read update or modify any details of a different account. This is true for creating new bank account, modifying details, or deleting data and information.