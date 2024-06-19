package com.Bank.managementSystem.DTO;

public class BalanceUpdateRequest {
    private int newBalance;
    private int transferId;
    private Long accountIdTo;
    private Long accountIdFrom;

    public Long getAccountIdTo() {
        return accountIdTo;
    }

    public void setAccountIdTo(Long accountIdTo) {
        this.accountIdTo = accountIdTo;
    }

    public Long getAccountIdFrom() {
        return accountIdFrom;
    }

    public void setAccountIdFrom(Long accountIdFrom) {
        this.accountIdFrom = accountIdFrom;
    }

    public int getNewBalance() {
        return newBalance;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public void setNewBalance(int newBalance) {
        this.newBalance = newBalance;
    }
}
