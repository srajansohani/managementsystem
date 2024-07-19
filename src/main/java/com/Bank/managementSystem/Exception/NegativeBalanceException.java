package com.Bank.managementSystem.Exception;

public class NegativeBalanceException extends RuntimeException {
    public NegativeBalanceException(String message) {
        super(message);
    }
}
