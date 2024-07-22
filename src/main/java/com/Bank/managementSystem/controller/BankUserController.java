package com.Bank.managementSystem.controller;

import com.Bank.managementSystem.DTO.*;
import com.Bank.managementSystem.Exception.*;
import com.Bank.managementSystem.Response.*;
import com.Bank.managementSystem.entity.Account;
import com.Bank.managementSystem.entity.Address;
import com.Bank.managementSystem.entity.BankUser;
import com.Bank.managementSystem.service.BankingServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class BankUserController {

    @Autowired
    private BankingServices service;

    private static final Logger LOGGER = Logger.getLogger(BankUserController.class.getName());

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/") //JSON Response
    public ResponseEntity<?> getUsers() {
        try {
            List<BankUser> all = service.getAll();
            LOGGER.log(Level.INFO, "Fetched all users successfully.");
            return ResponseEntity.ok(all);
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{id}") //JSON Response
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        try {
            Optional<BankUser> user = service.getUserById(id);
            return user.map(u -> {
                LOGGER.log(Level.INFO, "Fetched user with ID " + id + " successfully.");
                ApiResponse<BankUser> apiResponse = new ApiResponse<>("Fetched user with ID " + id + " successfully.", user.get());
                return ResponseEntity.ok(apiResponse);
            }).orElseThrow(() -> new NotFoundException("User with ID " + id + " not found."));
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{id}/accounts/{accountId}/balance") //JSON Response
    public ResponseEntity<?> getUserBalance(@PathVariable int id, @PathVariable Long accountId) {
        try {
            Optional<BankUser> user = service.getUserById(id);
            if (user.isPresent()) {
                String balance = user.get().getAccountBalance(accountId);
                if (balance != null) {
                    GetBalanceResponse getBalanceResponse = new GetBalanceResponse("Fetched balance for user ID " + id + " and account ID " + accountId + " successfully. : " + balance, user.get());
                    LOGGER.log(Level.INFO, "Fetched balance for user ID " + id + " and account ID " + accountId + " successfully.");
                    return ResponseEntity.ok(getBalanceResponse);
                } else {
                    throw new NotFoundException("Balance not found for account ID " + accountId + ".");
                }
            } else {
                throw new NotFoundException("User with ID " + id + " not found.");
            }
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/user/{id}/accounts/{accountId}/transactions")
    public ResponseEntity<?> getTransactionHistory(@PathVariable int id, @PathVariable Long accountId) {
        try {
            Optional<BankUser> user = service.getUserById(id);
            if (user.isPresent()) {
                List<String> transactions = service.getTransactionHistory(user.get(), accountId);
                if (transactions != null) {
                    LOGGER.log(Level.INFO, "Fetched transaction history for user ID " + id + " and account ID " + accountId + " successfully.");
                    return ResponseEntity.ok(transactions);
                } else {
                    throw new NotFoundException("Transactions not found for account ID " + accountId + ".");
                }
            } else {
                throw new NotFoundException("User with ID " + id + " not found.");
            }
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/user/{id}/accounts/{accountId}/{amountOfTransaction}/transactions")
    public ResponseEntity<?> getTransactionHistoryCustom(@PathVariable int id, @PathVariable Long accountId, @PathVariable int amountOfTransaction) {
        try {
            Optional<BankUser> user = service.getUserById(id);
            if (user.isPresent()) {
                List<String> transactions = service.getTransactionHistoryCustom(user.get(), accountId, amountOfTransaction);
                if (transactions != null) {
                    LOGGER.log(Level.INFO, "Fetched " + amountOfTransaction + " transaction history for user ID " + id + " and account ID " + accountId + " successfully.");
                    return ResponseEntity.ok(transactions);
                } else {
                    throw new NotFoundException("Transactions not found for account ID " + accountId + ".");
                }
            } else {
                throw new NotFoundException("User with ID " + id + " not found.");
            }
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/admin/") //JSON Response
    public ResponseEntity<?> addUser(@RequestBody CreateUserRequest createUserRequest) {
        try {
            boolean existingUser = createUserRequest.isExistingUser();
            if (!existingUser) {
                String name = createUserRequest.getName();
                String accountType = createUserRequest.getAccountType();
                String phone = String.valueOf(createUserRequest.getPhone());
                String email = createUserRequest.getEmail();

                if (name == null || name.isEmpty() || accountType == null || accountType.isEmpty() || (phone == null || phone.isEmpty()) && (email == null || email.isEmpty())) {
                    throw new InvalidInputException("Please enter all required fields: name, account type, and either phone or email.");
                }

                if (!accountType.equalsIgnoreCase("savings") && !accountType.equalsIgnoreCase("current")) {
                    throw new InvalidInputException("Invalid account type. Please choose either 'savings' or 'current'.");
                }

                Long phoneNumber = null;
                if (phone != null && !phone.isEmpty()) {
                    try {
                        phoneNumber = Long.parseLong(phone);
                        if (phoneNumber == 0) {
                            throw new InvalidInputException("Invalid phone number format.");
                        }
                    } catch (NumberFormatException e) {
                        throw new InvalidInputException("Invalid phone number format.");
                    }
                }

                try {
                    BankUser user = service.createUser(name, accountType, phoneNumber);
                    user.setMobileNumber(phoneNumber);
                    user.setEmail(email);
                    service.saveUser(user);
                    Account account = user.getAccounts().get(user.getAccounts().size() - 1);
                    CreationResponse creationResponse = new CreationResponse(name , accountType, account.getAccountId(), (long) user.getUserID());
                    return ResponseEntity.status(HttpStatus.CREATED).body(creationResponse);
                } catch (DuplicateAccountException ex) {
                    throw ex;
                }
            } else {
                String accountType = createUserRequest.getAccountType();
                int userId = createUserRequest.getExistingUserId();

                if (!accountType.equalsIgnoreCase("savings") && !accountType.equalsIgnoreCase("current")) {
                    throw new InvalidInputException("Invalid account type. Please choose either 'savings' or 'current'.");
                }

                Optional<BankUser> userOptional = service.getUserById(userId);
                if (userOptional.isPresent()) {
                    BankUser user = userOptional.get();
                    try {
                        user.addAccount(accountType);
                        service.saveUser(user);
                        return ResponseEntity.ok(user);
                    } catch (DuplicateAccountException ex) {
                        throw ex;
                    }
                } else {
                    throw new NotFoundException("User with ID " + userId + " not found.");
                }
            }
        } catch (InvalidInputException | DuplicateAccountException | NotFoundException ex) {
            return handleException(ex);
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/user/{userId}/accounts/{accountId}/updateAddress") //JSON Response
    public ResponseEntity<?> updateAddress(@PathVariable int userId, @PathVariable Long accountId, @RequestBody UpdateAddress updateAddress) {
        try {
            Address address = updateAddress.getAddress();
            Optional<BankUser> user = service.getUserById(userId);
            if (user.isPresent()) {
                user.get().setAddress(address);
                service.saveUser(user.get());
                LOGGER.log(Level.INFO, "Updated address for user ID " + userId + " and account ID " + accountId + " successfully.");
                ApiResponse<BankUser> apiResponse = new ApiResponse<>("Updated address for user ID " + userId + " and account ID " + accountId + " successfully with Address : " + address.toString(), user.get());
                return ResponseEntity.ok(apiResponse);
            } else {
                throw new NotFoundException("User with ID " + userId + " not found.");
            }
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/user/{userId}/accounts/{accountId}/updatePhone") //JSON Response
    public ResponseEntity<?> updatePhone(@PathVariable int userId, @PathVariable Long accountId, @RequestBody UpdatePhone updatePhone) {
        try {
            Long phone = updatePhone.getPhone();
            Optional<BankUser> user = service.getUserById(userId);
            if (user.isPresent()) {
                user.get().setMobileNumber(phone);
                service.saveUser(user.get());
                LOGGER.log(Level.INFO, "Updated phone number for user ID " + userId + " and account ID " + accountId + " successfully.");
                ApiResponse<BankUser> apiResponse = new ApiResponse<>("Updated phone number for user ID " + userId + " and account ID " + accountId + " successfully with phone : " + phone.toString(), user.get());
                return ResponseEntity.ok(apiResponse);
            } else {
                throw new NotFoundException("User with ID " + userId + " not found.");
            }
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/user/{userId}/accounts/{accountId}/updateEmail") //JSON Response
    public ResponseEntity<?> updateEmail(@PathVariable int userId, @PathVariable Long accountId, @RequestBody UpdateEmail updateEmail) {
        try {
            String email = updateEmail.getEmail();
            Optional<BankUser> user = service.getUserById(userId);
            if (user.isPresent()) {
                user.get().setEmail(email);
                service.saveUser(user.get());
                LOGGER.log(Level.INFO, "Updated email for user ID " + userId + " and account ID " + accountId + " successfully.");
                ApiResponse<BankUser> apiResponse = new ApiResponse<>("Updated email for user ID " + userId + " and account ID " + accountId + " successfully with email : " + email, user.get());
                return ResponseEntity.ok(apiResponse);
            } else {
                throw new NotFoundException("User with ID " + userId + " not found.");
            }
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}/accounts/{accountId}/balance") //JSON Response
    public ResponseEntity<?> updateUserBalance(@PathVariable int id, @PathVariable Long accountId, @RequestBody BalanceUpdateRequest balanceUpdateRequest) {
        try {
            int newBalance = balanceUpdateRequest.getNewBalance();
            if (newBalance < 0) {
                throw new NegativeBalanceException("Balance cannot be negative.");
            }

            Optional<BankUser> user = service.getUserById(id);
            if (user.isPresent()) {
                TransferArgument transferArgument = new TransferArgument(true);
                service.addBalance(user.get(), newBalance, accountId, transferArgument);
                LOGGER.log(Level.INFO, "Updated balance for user ID " + id + " and account ID " + accountId + " successfully.");
                ApiResponse<BankUser> apiResponse = new ApiResponse<>("Updated balance for user ID " + id + " and account ID " + accountId + " successfully.", user.get());
                return ResponseEntity.ok(apiResponse);
            } else {
                throw new NotFoundException("User with ID " + id + " not found.");
            }
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/user/{id}/accounts/{accountId}/balance/add") //JSON Response
    public ResponseEntity<?> addBalance(@PathVariable int id, @PathVariable Long accountId, @RequestBody AddBalance addBalance) {
        try {
            int balanceToAdd = addBalance.getBalanceToAdd();
            if (balanceToAdd < 0) {
                throw new NegativeBalanceException("Cannot add a negative balance.");
            }

            Optional<BankUser> user = service.getUserById(id);
            if (user.isPresent()) {
                TransferArgument transferArgument = new TransferArgument(true);
                boolean done = service.addBalance(user.get(), balanceToAdd, accountId, transferArgument);
                if (done) {
                    LOGGER.log(Level.INFO, "Added balance to user ID " + id + " and account ID " + accountId + " successfully.");
                    ApiResponse<BankUser> apiResponse = new ApiResponse<>("Added balance to user ID " + id + " and account ID " + accountId + " successfully.", user.get());
                    return ResponseEntity.ok(apiResponse);
                } else {
                    throw new Exception("Unable to add balance to account ID " + accountId + ".");
                }
            } else {
                throw new NotFoundException("User with ID " + id + " not found.");
            }
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/user/{id}/accounts/{accountId}/balance/withdraw")
    public ResponseEntity<?> withdrawBalance(@PathVariable int id, @PathVariable Long accountId, @RequestBody RemoveBalance removeBalance) {
        try {
            int balanceToRemove = removeBalance.getBalanceToRemove();
            if (balanceToRemove < 0) {
                throw new NegativeBalanceException("Cannot remove a negative balance.");
            }

            Optional<BankUser> user = service.getUserById(id);
            if (user.isPresent()) {
                if (Integer.parseInt(user.get().getAccountBalance(accountId)) < balanceToRemove) {
                    throw new InsufficientFundsException("Insufficient funds.");
                }

                TransferArgument transferArgument = new TransferArgument(true);
                boolean done = service.removeBalance(user.get(), balanceToRemove, accountId, transferArgument);
                if (done) {
                    RemoveResponse removeResponse = new RemoveResponse("Removed balance for user ID " + id + " and account ID " + accountId + " successfully.", user.get());
                    LOGGER.log(Level.INFO, "Removed balance for user ID " + id + " and account ID " + accountId + " successfully.");
                    return ResponseEntity.ok(removeResponse);
                } else {
                    throw new Exception("Unable to remove balance from account ID " + accountId + ".");
                }
            } else {
                throw new NotFoundException("User with ID " + id + " not found.");
            }
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/user/{id}/balance/transfer")
    public ResponseEntity<?> transferBalance(@PathVariable int id, @RequestBody TransferRequest transferRequest) {
        try {
            int balanceToTransfer = transferRequest.getBalanceToTransfer();
            int userToTransfer = transferRequest.getUserIdTO();
            Long fromAccountId = transferRequest.getAccountIdFrom();
            Long toAccountId = transferRequest.getAccountIdTo();

            if (balanceToTransfer < 0) {
                throw new NegativeBalanceException("Cannot transfer a negative balance.");
            }

            Optional<BankUser> fromUser = service.getUserById(id);
            Optional<BankUser> toUser = service.getUserById(userToTransfer);
            if (fromUser.isPresent() && toUser.isPresent()) {
                if (Integer.parseInt(fromUser.get().getAccountBalance(fromAccountId)) < balanceToTransfer) {
                    throw new InsufficientFundsException("Insufficient funds.");
                }

                boolean done = service.transferBetweenTwoAccounts(fromUser.get(), toUser.get(), fromAccountId, toAccountId, balanceToTransfer);
                if (done) {
                    TransferResponse response = new TransferResponse("Transferred balance from user ID " + id + " to user ID " + userToTransfer + " successfully.", fromUser.get());
                    LOGGER.log(Level.INFO, "Transferred balance from user ID " + id + " to user ID " + userToTransfer + " successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    throw new Exception("Transfer from account ID " + fromAccountId + " to account ID " + toAccountId + " failed.");
                }
            } else {
                throw new NotFoundException("User or account not found for transfer.");
            }
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable int id) {
        try {
            Optional<BankUser> user = service.getUserById(id);
            if (user.isPresent()) {
                service.deleteUser(id);
                LOGGER.log(Level.INFO, "Deleted user with ID " + id + " successfully.");
                return ResponseEntity.ok("User deleted successfully.");
            } else {
                throw new NotFoundException("User with ID " + id + " not found.");
            }
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    private ResponseEntity<ErrResponse> handleException(Exception ex) {
        if (ex instanceof NotFoundException) {
            return new ResponseEntity<>(new ErrResponse(ex.getMessage(), null), HttpStatus.NOT_FOUND);
        } else if (ex instanceof InvalidInputException) {
            return new ResponseEntity<>(new ErrResponse(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        } else if (ex instanceof DuplicateAccountException) {
            return new ResponseEntity<>(new ErrResponse(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        } else if (ex instanceof NegativeBalanceException) {
            return new ResponseEntity<>(new ErrResponse(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        } else if (ex instanceof InsufficientFundsException) {
            return new ResponseEntity<>(new ErrResponse(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(new ErrResponse("An error occurred: " + ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void logNotFound(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    private void logError(String message) {
        LOGGER.log(Level.SEVERE, message);
    }
}