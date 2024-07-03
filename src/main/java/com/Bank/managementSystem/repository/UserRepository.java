package com.Bank.managementSystem.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Bank.managementSystem.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
