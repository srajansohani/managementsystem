package com.Bank.managementSystem.entity;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long AccountId;

    private String AccountType;
    private String AccountBalance;

    @ElementCollection
    @CollectionTable(name = "transaction", joinColumns = @JoinColumn(name = "account_id"))
    private List<Transaction> transaction;

    //Constructors
    public Account() {
    }

    // To add a account with some initial balance.
    public Account(String accountType, String accountBalance) {
        this.AccountType = accountType;
        this.AccountBalance = accountBalance;
    }

    // To add a account with 0 balance.
    public Account(String accountType) {
        this.AccountType = accountType;
        this.AccountBalance = "0";
    }

    public void setAccountBalance(int accountBalance) {
        this.AccountBalance = String.valueOf(accountBalance);
    }

    public String getAccountBalance() {
        return this.AccountBalance;
    }

    public Long getAccountId() {
        return this.AccountId;
    }

    public void setAccountId(Long accountId) {
        this.AccountId = accountId;
    }

    public String getAccountType() {
        return this.AccountType;
    }

    public void setAccountType(String accountType) {
        this.AccountType = accountType;
    }

    public List<Transaction> getTransaction() {
        return this.transaction;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transaction = transactions;
    }

    public boolean addTransaction(Transaction transaction){
        return this.transaction.add(transaction);
    }
}
