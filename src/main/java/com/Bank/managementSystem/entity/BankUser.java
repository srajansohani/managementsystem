package com.Bank.managementSystem.entity;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

@Entity
public class BankUser {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int userID;

    public BankUser(int userID, String name, Long mobileNumber, String email, Address address) {
        this.userID = userID;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.address = address;
    }

    private String name;

    public BankUser(int userID, String name, Long mobileNumber, String email) {
        this.userID = userID;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.email = email;
    }

    public BankUser(int userID, String name, Long mobileNumber) {
        this.userID = userID;
        this.name = name;
        this.mobileNumber = mobileNumber;
    }

    private Long mobileNumber;
    private String email;

    @Embedded
    private Address address;

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public long setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
        return 0;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public Address setAddress(Address address) {
        this.address = address;
        return address;
    }
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
