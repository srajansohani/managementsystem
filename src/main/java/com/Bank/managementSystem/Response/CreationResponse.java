package com.Bank.managementSystem.Response;

public class CreationResponse {
    public CreationResponse(String name, String accountType, Long accountId, long userID) {
        this.accountId = accountId;
        this.userId = userID;
        this.accountType = accountType;
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CreationResponse(String message, Long accountId, Long userId) {
        this.message = message;
        this.accountId = accountId;
        this.userId = userId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getName() {
        return name;
    }

    public CreationResponse(Long accountId, Long userId, String accountType, String name) {
        this.accountId = accountId;
        this.userId = userId;
        this.accountType = accountType;
        this.name = name;
    }

    public CreationResponse(String message, Long accountId, Long userId, String accountType, String name) {
        this.message = message;
        this.accountId = accountId;
        this.userId = userId;
        this.accountType = accountType;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String message;
    private Long accountId;
    private Long userId;
    private String accountType;
    private String name;
}

