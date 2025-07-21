package com.eaglebank.eagle_bank_api;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import com.eaglebank.eagle_bank_api.dto.BankAccountCreateRequest;
import com.eaglebank.eagle_bank_api.dto.TransactionCreateRequest;
import com.eaglebank.eagle_bank_api.dto.UserCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BankIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void completeUserJourney_Success() throws Exception {
        String baseUrl = "http://localhost:" + port + "/api";
        
        // Step 1: Create a new user
        UserCreateRequest userRequest = new UserCreateRequest();
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setEmail("john.doe@example.com");
        userRequest.setPhoneNumber("1234567890");
        userRequest.setAddress("123 Main St");

        var userResponse = restTemplate.postForEntity(
            baseUrl + "/v1/users", 
            userRequest, 
            String.class
        );
        
        assert userResponse.getStatusCode().is2xxSuccessful();
        String userId = objectMapper.readTree(userResponse.getBody()).get("userId").asText();

        // Step 2: Create a bank account
        BankAccountCreateRequest accountRequest = new BankAccountCreateRequest();
        accountRequest.setAccountName("Savings Account");
        accountRequest.setAccountType("SAVINGS");
        accountRequest.setInitialBalance(new BigDecimal("1000.00"));

        var headers = new org.springframework.http.HttpHeaders();
        headers.set("X-User-Id", userId);
        var accountEntity = new org.springframework.http.HttpEntity<>(accountRequest, headers);
        
        var accountResponse = restTemplate.postForEntity(
            baseUrl + "/v1/accounts", 
            accountEntity, 
            String.class
        );
        
        assert accountResponse.getStatusCode().is2xxSuccessful();
        String accountId = objectMapper.readTree(accountResponse.getBody()).get("accountId").asText();

        // Step 3: Make a deposit
        TransactionCreateRequest depositRequest = new TransactionCreateRequest();
        depositRequest.setAmount(new BigDecimal("500.00"));
        depositRequest.setTransactionType("DEPOSIT");
        depositRequest.setDescription("Salary deposit");

        var depositEntity = new org.springframework.http.HttpEntity<>(depositRequest, headers);
        
        var depositResponse = restTemplate.postForEntity(
            baseUrl + "/v1/accounts/" + accountId + "/transactions", 
            depositEntity, 
            String.class
        );
        
        assert depositResponse.getStatusCode().is2xxSuccessful();
        
        // Verify balance is 1500.00
        var balanceCheckEntity = new org.springframework.http.HttpEntity<>(headers);
        var balanceResponse = restTemplate.exchange(
            baseUrl + "/v1/accounts/" + accountId,
            org.springframework.http.HttpMethod.GET,
            balanceCheckEntity,
            String.class
        );
        
        assert balanceResponse.getStatusCode().is2xxSuccessful();
        String balance = objectMapper.readTree(balanceResponse.getBody()).get("balance").asText();
        assert "1500.0".equals(balance) || "1500.00".equals(balance);
    }

    @Test
    public void simpleUserCreation_Success() throws Exception {
        String baseUrl = "http://localhost:" + port + "/api";
        
        // Create a new user
        UserCreateRequest userRequest = new UserCreateRequest();
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setEmail("john.doe@example.com");
        userRequest.setPhoneNumber("1234567890");
        userRequest.setAddress("123 Main St");

        var userResponse = restTemplate.postForEntity(
            baseUrl + "/v1/users", 
            userRequest, 
            String.class
        );
        
        assert userResponse.getStatusCode().is2xxSuccessful();
        assert userResponse.getBody().contains("john.doe@example.com");
    }
}