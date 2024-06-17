package com.Bank.managementSystem.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.Bank.managementSystem.entity.BankUser;

public interface BankUserRepository extends JpaRepository<BankUser, Integer> {}

