package com.Bank.managementSystem.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long AccountId;
    private String AccountType;
    private String AccountBalance;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private BankUser bankUser;

    @ElementCollection
    @CollectionTable(name = "transaction", joinColumns = @JoinColumn(name = "account_id"))
    private List<Transaction> transaction;

    //Constructors
    public Account() {
    }

//    // To add a account with some initial balance.
//    public Account(String accountType, String accountBalance) {
//        this.AccountType = accountType;
//        this.AccountBalance = accountBalance;
//    }
//
//    public Account(String accountType, String accountBalance,BankUser b1) {
//        this.AccountType = accountType;
//        this.AccountBalance = accountBalance;
//        this.bankUser = b1;
//    }

    public BankUser getBankUser() {
        return bankUser;
    }

    public void setBankUser(BankUser bankUser) {
        this.bankUser = bankUser;
    }

    // To add a account with 0 balance.
    public Account(String accountType) {
        this.AccountType = accountType;
        this.AccountBalance = "0";
    }

    public Account(String accountType,BankUser b1) {
        this.AccountType = accountType;
        this.AccountBalance = "0";
        this.bankUser = b1;
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
