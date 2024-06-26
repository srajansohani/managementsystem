package com.Bank.managementSystem;
import com.Bank.managementSystem.repository.bankUserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private bankUserManager bankUserManager;

    @Override
    public void run(String... args) throws Exception {
        bankUserManager.clearTable();
    }
}

