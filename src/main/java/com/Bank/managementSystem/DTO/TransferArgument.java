package com.Bank.managementSystem.DTO;

public class TransferArgument {
    boolean self;
    private long accountId;

    public boolean isSelf() {
        return self;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public TransferArgument(boolean self) {
        this.self = self;
    }

    public TransferArgument(boolean self, long accountId) {
        this.self = self;
        this.accountId = accountId;
    }

    public void setSelf(boolean self) {
        this.self = self;
    }
}
