package com.eaglebank.eagle_bank_api.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private List<String> errors;
    
    // Constructors
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(String message, int status) {
        this();
        this.message = message;
        this.status = status;
    }
    
    public ErrorResponse(String message, int status, List<String> errors) {
        this(message, status);
        this.errors = errors;
    }
    
    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
