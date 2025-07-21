package com.eaglebank.eagle_bank_api.enums;

public enum TransactionType {
    DEPOSIT("DEPOSIT"),
    WITHDRAWAL("WITHDRAWAL");
    
    private final String value;
    
    TransactionType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static TransactionType fromValue(String value) {
        for (TransactionType type : TransactionType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid transaction type: " + value);
    }
}