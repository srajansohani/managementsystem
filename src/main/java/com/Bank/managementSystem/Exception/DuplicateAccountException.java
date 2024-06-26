package com.Bank.managementSystem.Exception;

public class DuplicateAccountException extends RuntimeException{
    public DuplicateAccountException(String message) {
        super(message);
    }
}
