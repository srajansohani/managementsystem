package com.Bank.managementSystem.Response;

import com.Bank.managementSystem.entity.BankUser;

public class GetBalanceResponse {
    private String message;
    private BankUser userAccount;

    public GetBalanceResponse(String message, BankUser userAccount) {
        this.message = message;
        this.userAccount = userAccount;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BankUser getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(BankUser userAccount) {
        this.userAccount = userAccount;
    }
}
