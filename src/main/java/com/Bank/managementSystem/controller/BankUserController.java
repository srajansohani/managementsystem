package com.Bank.managementSystem.controller;
import com.Bank.managementSystem.Response.*;
import java.util.List;
import java.util.Optional;

import com.Bank.managementSystem.DTO.BalanceUpdateRequest;
import com.Bank.managementSystem.entity.Address;
import com.Bank.managementSystem.entity.BankUser;
import com.Bank.managementSystem.service.BankingServices;
import com.Bank.managementSystem.DTO.*;
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
    public ResponseEntity<GetBalanceResponse> getUserBalance(@PathVariable int id, @PathVariable Long accountId) {
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            String balance = user.get().getAccountBalance(accountId);
            if (balance != null) {
                GetBalanceResponse getBalanceResponse = new GetBalanceResponse("User balance : " + balance, user.get());
                return ResponseEntity.ok(getBalanceResponse);
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

    @PostMapping("/{userId}/accounts/{accountId}/updateAddress")
    public ResponseEntity<BankUser> updateAddress(@PathVariable int id, @PathVariable Long accountId, @RequestBody UpdateAddress updateAddress){
        Address address = updateAddress.getAddress();
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
             user.get().setAddress(address);
             service.saveUser(user.get());
             return ResponseEntity.ok(user.get());
        }
        return null;
    }

    @PostMapping("/{userId}/accounts/{accountId}/updatePhone")
    public ResponseEntity<BankUser> updatePhone(@PathVariable int id, @PathVariable Long accountId, @RequestBody UpdatePhone updatePhone){
        Long phone = updatePhone.getPhone();
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            user.get().setMobileNumber(phone);
            service.saveUser(user.get());
            return ResponseEntity.ok(user.get());
        }
        return null;
    }

    @PostMapping("/{userId}/accounts/{accountId}/updateEmail")
    public ResponseEntity<BankUser> updateEmail(@PathVariable int id, @PathVariable Long accountId, @RequestBody UpdateEmail updateEmail){
        String email = updateEmail.getEmail();
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            user.get().setEmail(email);
            service.saveUser(user.get());
            return ResponseEntity.ok(user.get());
        }
        return null;
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
    public ResponseEntity<BankUser> addBalance(@PathVariable int id, @PathVariable Long accountId, @RequestBody AddBalance addBalance) {
        int balanceToAdd = addBalance.getBalanceToAdd();
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
    public ResponseEntity<RemoveResponse> removeBalance(@PathVariable int id, @PathVariable Long accountId, @RequestBody RemoveBalance removeBalance) {
        int balanceToRemove = removeBalance.getBalanceToRemove();
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            boolean done = service.removeBalance(user.get(), balanceToRemove, accountId);
            if (done) {
                RemoveResponse removeResponse = new RemoveResponse("Balance Removed Successfully", user.get());
                return ResponseEntity.ok(removeResponse);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/balance/transfer")
    public ResponseEntity<TransferResponse> transferBalance(@PathVariable int id, @RequestBody TransferRequest transferRequest){
        int balanceToTransfer = transferRequest.getBalanceToTransfer();
        int userToTransfer = transferRequest.getUserIdTO();
        Long fromAccountId = transferRequest.getAccountIdFrom();
        Long toAccountId = transferRequest.getAccountIdTo();

        Optional<BankUser> fromUser = service.getUserById(id);
        Optional<BankUser> toUser = service.getUserById(userToTransfer);
        if (fromUser.isPresent() && toUser.isPresent()) {
            boolean done = service.transferBetweenTwoAccounts(fromUser.get(), toUser.get(),fromAccountId, toAccountId, balanceToTransfer);
            if (done) {
                TransferResponse response = new TransferResponse("Transfer completed successfully.", fromUser.get());
                return ResponseEntity.ok(response);
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
