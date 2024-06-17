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

    public boolean addBalance(BankUser b1, int balanceToAdd) {
        if (balanceToAdd <= 0) {
            System.out.println("Failed to add balance. The amount to add must be positive.");
            return false;
        }
        int newBalance = b1.getUserBalance() + balanceToAdd;
        b1.setUserBalance(newBalance);
    
        boolean transactionSuccessful = b1.addTransaction(new Transactions("+", balanceToAdd));
        if (transactionSuccessful) {
            System.out.println("Successfully added balance to the account.");
            System.out.println(String.format("New Balance : %d", newBalance));
            repository.update(b1); // Save the updated user to the repository
            return true;
        } else {
            b1.setUserBalance(b1.getUserBalance() - balanceToAdd);
            System.out.println("Failed to record the transaction.");
            return false;
        }
    }

    public boolean removeBalance(BankUser b1, int balanceToRemove) {
        boolean done = false;
        if (balanceToRemove > b1.getUserBalance()) {
            System.err.println("Insufficient Funds");
            System.out.println("Available Funds " + b1.getUserBalance());
            return false;
        } else if (b1.getUserBalance() >= balanceToRemove) {
            int newBalance = b1.getUserBalance() - balanceToRemove;
            b1.setUserBalance(newBalance);
            done = true;
        }
        if (done) {
            b1.addTransaction(new Transactions("-", balanceToRemove));
            repository.update(b1); // Save the updated user to the repository
            System.out.println("Successfully removed balance from the account.");
            System.out.println(String.format("New Balance : %d", b1.getUserBalance()));
        }
        return done;
    }

    public boolean transferBetweenTwoAccounts(BankUser b1, BankUser toUser, int amount) {
        if (b1.getUserBalance() < amount) {
            System.err.println("Unsuccessful Transaction: User balance low.");
            return false;
        }
        if (removeBalance(b1, amount) && addBalance(toUser, amount)) {
            System.out.println("Successfully transferred " + amount + " from " + b1.getUserAccount() + " to " + toUser.getUserAccount());
            return true;
        }
        return false;
    }

    public List<String> getTransactionHistory(BankUser b1) {
        return b1.getTransactions().stream()
                           .map(Transactions::toString)
                           .collect(Collectors.toList());
    }

    public Optional<BankUser> getUserById(int id) {
        return Optional.ofNullable(repository.findById(id));
    }

    public void saveUser(BankUser user) {repository.update(user);
    }

    public BankUser createUser(String name) {return repository.create(name);}

    public void deleteUser(int id){
        repository.delete(id);
    }

    public void updateUser(BankUser user){
        repository.update(user);
    }
}
