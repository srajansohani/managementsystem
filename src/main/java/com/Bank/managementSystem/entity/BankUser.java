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
    private String userAccount;
    private int userBalance;

    @ElementCollection
    @CollectionTable(name = "transactions", joinColumns = @JoinColumn(name = "user_id"))
    private List<Transactions> transactions;

    public BankUser(){}

    //When we do not pass the user ID into the constructor argument it should refer to this one
    public BankUser(String name){
        this.name = name;
        this.userAccount = "User#"+userID;
        this.userBalance = 0;
        transactions = new ArrayList<Transactions>();
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public int getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(int userBalance) {
        this.userBalance = userBalance;
    }

    public List<Transactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transactions> transactions) {
        this.transactions = transactions;
    }

    public boolean addTransaction(Transactions transaction){
        boolean success = this.transactions.add(transaction);
        return success;
    }

    public int get(){
        return this.userID;
    }

    public int getBalance(){
        int balance = this.userBalance;
        return balance;
    }

    public String getAccount(int userID){
        String account = userAccount;
        System.out.println(account);
        return account;
    }
}
