package com.Bank.managementSystem.DTO;

public class CreateUserRequest {
        private String name;
        private String accountType;
        private long phone;

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;
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
