package com.Bank.managementSystem.service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.Bank.managementSystem.DTO.TransferArgument;
import com.Bank.managementSystem.entity.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Bank.managementSystem.entity.BankUser;
import com.Bank.managementSystem.entity.Transaction;
import com.Bank.managementSystem.repository.bankUserManager;

@Service
public class BankingServices{

    @Autowired
    private bankUserManager repository;

    public boolean addBalance(BankUser b1, int balanceToAdd, Long accountId, TransferArgument transferArgument) {
        if (balanceToAdd <= 0) {
            System.out.println("Failed to add balance. The amount to add must be positive.");
            return false;
        }
        int prevBalance = Integer.parseInt(b1.getAccountBalance(accountId));
        int newBalance = prevBalance + balanceToAdd;
        Long accountIdFrom = null;
        if (!transferArgument.isSelf()){
            accountIdFrom = accountId;
        }
        b1.setAccountBalance(accountId,balanceToAdd);
        boolean transactionSuccessful;
        if (transferArgument.isSelf()){
//            transactionSuccessful = b1.getAccount(accountId).addTransaction(new Transaction("Credit - ", balanceToAdd, LocalDateTime.now(), accountId));
            transactionSuccessful = b1.getAccount(accountId).addTransaction(new Transaction("Credit - ", balanceToAdd, LocalDateTime.now(), accountId));
        }
        else {
             transactionSuccessful = b1.getAccount(accountId).addTransaction(new Transaction("Credit - ", balanceToAdd, LocalDateTime.now(),transferArgument.getAccountId(),accountId));
        }
        if (transactionSuccessful) {
            System.out.println("Successfully added balance to the account.");
            System.out.println(String.format("New Balance : %d", newBalance));
            repository.update(b1); // Save the updated user to the repository
            return true;
        } else {
            b1.setAccountBalance(accountId,Integer.parseInt(b1.getAccountBalance(accountId)) - balanceToAdd);
            System.out.println("Failed to record the transaction.");
            return false;
        }
    }

    public boolean removeBalance(BankUser b1, int balanceToRemove, Long accountId, TransferArgument transferArgument) {
        boolean done = false;
        if (balanceToRemove > Integer.parseInt(b1.getAccountBalance(accountId))) {
            System.err.println("Insufficient Funds");
            System.out.println("Available Funds " + b1.getAccountBalance(accountId));
            return false;
        }
        else if (Integer.parseInt(b1.getAccountBalance(accountId)) >= balanceToRemove) {
            int newBalance = Integer.parseInt(b1.getAccountBalance(accountId)) - balanceToRemove;
            b1.setAccountBalance(accountId,newBalance);
            done = true;
        }
        if (done) {
            long accountIdTo = 0;
            if (!transferArgument.isSelf()){
                accountIdTo = transferArgument.getAccountId();
            }
            boolean transactionSuccessful;
            if (transferArgument.isSelf()){
                transactionSuccessful = b1.getAccount(accountId).addTransaction(new Transaction("Debit - ", balanceToRemove, LocalDateTime.now(), accountId));
            }
            else {
                transactionSuccessful = b1.getAccount(accountId).addTransaction(new Transaction("Debit - ", balanceToRemove, LocalDateTime.now(), accountId, accountIdTo));
            }
//            b1.getAccount(accountId).addTransaction(new Transactions("Debit - ", balanceToRemove,LocalDateTime.now()));
            repository.update(b1); // Save the updated user to the repository
            System.out.println("Successfully removed balance from the account.");
            System.out.println(String.format("New Balance : %d", Integer.parseInt(b1.getAccountBalance(accountId))));
        }
        return done;
    }

    public boolean transferBetweenTwoAccounts(BankUser b1, BankUser toUser,Long accountIdfrom, Long accountIdTo,  int amount) {
        if (Integer.parseInt(b1.getAccountBalance(accountIdfrom)) < amount) {
            System.err.println("Unsuccessful Transaction: User balance low.");
            return false;
        }
        TransferArgument transferDebit = new TransferArgument(false,accountIdTo);
        TransferArgument transferCredit = new TransferArgument(false,accountIdfrom);
        if (removeBalance(b1, amount, accountIdfrom , transferDebit) && addBalance(toUser, amount, accountIdTo , transferCredit)) {
            System.out.println("Successfully transferred " + amount + " from " + b1.getAccount(accountIdfrom) + " to " + toUser.getAccount(accountIdTo));
            repository.update(b1);
            repository.update(toUser);
            return true;
        }
        return false;
    }

    public List<String> getTransactionHistory(BankUser b1, Long accountId) {
        return b1.getAccount(accountId).getTransaction().stream()
                           .map(Transaction::toString)
                           .collect(Collectors.toList());
    }

    public List<String> getTransactionHistoryCustom(BankUser user, Long accountId, int limit) {
        return user.getAccount(accountId).getTransaction().stream()
                .sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate())) // Sort by date, descending
                .limit(limit) // Limit to the specified number of transactions
                .map(Transaction::toString)
                .collect(Collectors.toList());
    }

    public Optional<BankUser> getUserById(int id) {
        return Optional.ofNullable(repository.findById(id));
    }

    public BankUser saveUser(BankUser user) {
        repository.update(user);
        return user;
    }

    public BankUser createUser(String name, String accountType, Long phone) {
        return repository.create(name,accountType,phone);
    }

    public void deleteUser(int id){
        repository.delete(id);
    }

    public List<BankUser> getAll(){
        return repository.findAll();
    }
}
