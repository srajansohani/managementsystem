package com.Bank.managementSystem.DTO;

public class TransferRequest {
    private Long accountIdTo;
    private int balanceToTransfer;
    private int userIdTO;

    public int getUserIdTO() {
        return userIdTO;
    }

    public void setUserIdTO(int userIdTO) {
        this.userIdTO = userIdTO;
    }

    public int getBalanceToTransfer() {
        return balanceToTransfer;
    }

    public void setBalanceToTransfer(int balanceToTransfer) {
        this.balanceToTransfer = balanceToTransfer;
    }

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

    private Long accountIdFrom;
}
