package com.Bank.managementSystem.controller;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Bank.managementSystem.entity.BankUser;
import com.Bank.managementSystem.service.BankingServices;

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

    @GetMapping("/{id}/balance")
    public ResponseEntity<Integer> getUserBalance(@PathVariable int id) {
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get().getUserBalance());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<String>> getTransactionHistory(@PathVariable int id) {
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(service.getTransactionHistory(user.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }    

    // Add a new user (input data)
    @PostMapping("/")
    public ResponseEntity<BankUser> addUser(@RequestBody BankUser user) {
        service.saveUser(user);
        return ResponseEntity.ok(user);
    }

    // Update user balance (input data)
    @PutMapping("/{id}/balance")
    public ResponseEntity<String> updateUserBalance(@PathVariable int id, @RequestBody int newBalance) {
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            BankUser existingUser = user.get();
            existingUser.setUserBalance(newBalance);
            service.saveUser(existingUser);
            return ResponseEntity.ok("User balance updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a user by ID (input data)
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
