package com.Bank.managementSystem.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class BankUser {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int userID;
    private String name;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "account_id")
//    private Account userAccount;

    public BankUser(){}

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")  // this is the foreign key in the Account table
    private List<Account> accounts = new ArrayList<>();

    //When we do not pass the user ID into the constructor argument it should refer to this one
    public BankUser(String name, String accountType){
        this.name = name;
        Account account = new Account(accountType);
        this.accounts.add(account);
    }

    public int getUserID() {
        return this.userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }

    public List<Account> getUserAccounts() {
        return accounts;
    }

    public Account getAccount(Long accountId) {
        for (Account account : accounts) {
            if (account.getAccountId().equals(accountId)) {
                return account;
            }
        }
        return null;
    }

    public Account setAccountBalance(Long accountId, int balanceToSet){
        for (Account account: accounts){
            if (account.getAccountId().equals(accountId)){
                account.setAccountBalance(balanceToSet);
            }
        }
        return null;
    }
    public String getAccountBalance(Long accountId) {
        for (Account account : accounts) {
            if (account.getAccountId().equals(accountId)) {
                return account.getAccountBalance();
            }
        }
        return null;
    }


    public List<Transactions> getUserTransactions(Long accountId) {
        for (Account account : accounts) {
            if (account.getAccountId().equals(accountId)) {
                return account.getTransactions();
            }
        }
        return null; // Or throw an exception if account not found
    }

    public int get(){
        return this.userID;
    }

    public List<Account> getAccounts(int userID){
        return accounts;
    }
}
