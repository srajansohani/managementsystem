package com.Bank.managementSystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.Bank.managementSystem.entity.BankUser;
import com.Bank.managementSystem.service.BankingServices;

@SpringBootApplication
public class ManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManagementSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(BankingServices bankingServices) {
        return (args) -> {
            // Create some initial users
            BankUser user1 = new BankUser();
            user1.setUserID(1);
			
            user1.setUserBalance(1000);

            BankUser user2 = new BankUser();
            user2.setUserID(2);
            user2.setUserBalance(500);

            bankingServices.saveUser(user1);
            bankingServices.saveUser(user2);

            // Add some transactions
            bankingServices.addBalance(user1, 200);
            bankingServices.addBalance(user2, 300);
            bankingServices.removeBalance(user1, 100);
            bankingServices.transferBetweenTwoAccounts(user1, user2, 150);

            // Display initial data
            System.out.println("User1: " + bankingServices.getUserById(1));
            System.out.println("User2: " + bankingServices.getUserById(2));
        };
    }
}


