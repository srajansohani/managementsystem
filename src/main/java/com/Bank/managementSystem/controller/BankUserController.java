package com.Bank.managementSystem.controller;

import java.util.List;
import java.util.Optional;

import com.Bank.managementSystem.DTO.BalanceUpdateRequest;
import com.Bank.managementSystem.entity.BankUser;
import com.Bank.managementSystem.service.BankingServices;
import com.Bank.managementSystem.DTO.CreateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class BankUserController {

    @Autowired
    private BankingServices service;

    @GetMapping("/{id}")
    public ResponseEntity<BankUser> getUserById(@PathVariable int id) {
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/accounts/{accountId}/balance")
    public ResponseEntity<String> getUserBalance(@PathVariable int id, @PathVariable Long accountId) {
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            String balance = user.get().getAccountBalance(accountId);
            if (balance != null) {
                return ResponseEntity.ok(balance);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/accounts/{accountId}/transactions")
    public ResponseEntity<List<String>> getTransactionHistory(@PathVariable int id, @PathVariable Long accountId) {
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            List<String> transactions = service.getTransactionHistory(user.get(), accountId);
            if (transactions != null) {
                return ResponseEntity.ok(transactions);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    public ResponseEntity<BankUser> addUser(CreateUserRequest createUserRequest) {
        String name = createUserRequest.getName();
        String accountType = createUserRequest.getAccountType();
        BankUser user = service.createUser(name, accountType);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{id}/accounts/{accountId}/balance")
    public ResponseEntity<String> updateUserBalance(@PathVariable int id, @PathVariable Long accountId, @RequestBody BalanceUpdateRequest balanceUpdateRequest) {
        String newBalance = String.valueOf(balanceUpdateRequest.getNewBalance());
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            service.addBalance(user.get(), Integer.parseInt(newBalance),accountId);
            return ResponseEntity.ok("User balance updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/accounts/{accountId}/balance/add")
    public ResponseEntity<BankUser> addBalance(@PathVariable int id, @PathVariable Long accountId, @RequestBody BalanceUpdateRequest balanceUpdateRequest) {
        int balanceToAdd = balanceUpdateRequest.getNewBalance();
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            boolean done = service.addBalance(user.get(), balanceToAdd, accountId);
            if (done) {
                return ResponseEntity.ok(user.get());
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/accounts/{accountId}/balance/remove")
    public ResponseEntity<BankUser> removeBalance(@PathVariable int id, @PathVariable Long accountId, @RequestBody BalanceUpdateRequest balanceUpdateRequest) {
        int balanceToRemove = balanceUpdateRequest.getNewBalance();
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            boolean done = service.removeBalance(user.get(), balanceToRemove, accountId);
            if (done) {
                return ResponseEntity.ok(user.get());
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/balance/transfer")
    public ResponseEntity<String> transferBalance(@PathVariable int id, @RequestBody BalanceUpdateRequest balanceUpdateRequest){
        int balanceToTransfer = balanceUpdateRequest.getNewBalance();
        int userToTransfer = balanceUpdateRequest.getTransferId();
        Long fromAccountId = balanceUpdateRequest.getAccountIdFrom();
        Long toAccountId = balanceUpdateRequest.getAccountIdTo();

        Optional<BankUser> fromUser = service.getUserById(id);
        Optional<BankUser> toUser = service.getUserById(userToTransfer);
        if (fromUser.isPresent() && toUser.isPresent()) {
            boolean done = service.transferBetweenTwoAccounts(fromUser.get(), toUser.get(),fromAccountId, toAccountId, balanceToTransfer);
            if (done) {
                return ResponseEntity.ok("Transfer completed successfully.");
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable int id) {
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            service.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
