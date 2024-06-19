package com.Bank.managementSystem.service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Bank.managementSystem.entity.BankUser;
import com.Bank.managementSystem.entity.Transactions;
import com.Bank.managementSystem.repository.bankUserManager;

@Service
public class BankingServices{

    @Autowired
    private bankUserManager repository;

    public boolean addBalance(BankUser b1, int balanceToAdd, Long accountId) {
        if (balanceToAdd <= 0) {
            System.out.println("Failed to add balance. The amount to add must be positive.");
            return false;
        }
        int prevBalance = Integer.parseInt(b1.getAccountBalance(accountId));
        int newBalance = prevBalance + balanceToAdd;
        b1.setAccountBalance(accountId,balanceToAdd);
        boolean transactionSuccessful = b1.getAccount(accountId).addTransaction(new Transactions("Credit", balanceToAdd));
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

    public boolean removeBalance(BankUser b1, int balanceToRemove, Long accountId) {
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
            b1.getAccount(accountId).addTransaction(new Transactions("Debit", balanceToRemove));
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
        if (removeBalance(b1, amount, accountIdfrom) && addBalance(toUser, amount, accountIdTo)) {
            System.out.println("Successfully transferred " + amount + " from " + b1.getAccount(accountIdfrom) + " to " + toUser.getAccount(accountIdTo));
            repository.update(b1);
            repository.update(toUser);
            return true;

        }
        return false;
    }

    public List<String> getTransactionHistory(BankUser b1, Long accountId) {
        return b1.getAccount(accountId).getTransactions().stream()
                           .map(Transactions::toString)
                           .collect(Collectors.toList());
    }

    public Optional<BankUser> getUserById(int id) {
        return Optional.ofNullable(repository.findById(id));
    }

    public void saveUser(BankUser user) {repository.update(user);
    }

    public BankUser createUser(String name, String accountType) {return repository.create(name,accountType);}

    public void deleteUser(int id){
        repository.delete(id);
    }

    public void updateUser(BankUser user, Long accountId){
        repository.update(user);
    }
}
