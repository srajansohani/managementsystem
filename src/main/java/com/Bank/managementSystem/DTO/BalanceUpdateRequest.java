package com.Bank.managementSystem.DTO;

public class BalanceUpdateRequest {
    private int newBalance;
    private int transferId;

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
