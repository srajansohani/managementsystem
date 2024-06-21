package com.Bank.managementSystem.DTO;

public class CreateUserRequest {
        private String name;
        private String accountType;
        private boolean existingUser;
        private int existingUserId;

    public int getExistingUserId() {
        return existingUserId;
    }

    public void setExistingUserId(int existingUserId) {
        this.existingUserId = existingUserId;
    }

    public boolean isExistingUser() {
            return existingUser;
        }

        public void setExistingUser(boolean existingUser) {
            this.existingUser = existingUser;
        }

    // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAccountType() {
            return accountType;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }
    }
