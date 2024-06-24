package com.Bank.managementSystem.controller;

import com.Bank.managementSystem.DTO.*;
import com.Bank.managementSystem.Response.*;
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
    @GetMapping("/admin/")
    public ResponseEntity<List<BankUser>> getUsers() {
        List<BankUser> all = service.getAll();
        LOGGER.log(Level.INFO, "Fetched all users successfully.");
        return ResponseEntity.ok(all);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        Optional<BankUser> user = service.getUserById(id);
        return user.map(u -> {
            LOGGER.log(Level.INFO, "Fetched user with ID " + id + " successfully.");
            ApiResponse apiResponse = new ApiResponse<>("Fetched user with ID " + id + " successfully.",user.get());
            return ResponseEntity.ok(apiResponse);
        }).orElseGet(() -> {
            logNotFound("User with ID " + id + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        });
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{id}/accounts/{accountId}/balance")
    public ResponseEntity<GetBalanceResponse> getUserBalance(@PathVariable int id, @PathVariable Long accountId) {
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            String balance = user.get().getAccountBalance(accountId);
            if (balance != null) {
                GetBalanceResponse getBalanceResponse = new GetBalanceResponse("Fetched balance for user ID " + id + " and account ID " + accountId + " successfully. : " + balance, user.get());
                LOGGER.log(Level.INFO, "Fetched balance for user ID " + id + " and account ID " + accountId + " successfully.");
                return ResponseEntity.ok(getBalanceResponse);
            } else {
                logNotFound("Balance not found for account ID " + accountId + ".");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            logNotFound("User with ID " + id + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/user/{id}/accounts/{accountId}/transactions")
    public ResponseEntity<List<String>> getTransactionHistory(@PathVariable int id, @PathVariable Long accountId) {
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            List<String> transactions = service.getTransactionHistory(user.get(), accountId);
            if (transactions != null) {
                LOGGER.log(Level.INFO, "Fetched transaction history for user ID " + id + " and account ID " + accountId + " successfully.");
                return ResponseEntity.ok(transactions);
            } else {
                logNotFound("Transactions not found for account ID " + accountId + ".");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            logNotFound("User with ID " + id + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/admin/")
    public ResponseEntity<?> addUser(@RequestBody CreateUserRequest createUserRequest) {
        boolean existingUser = createUserRequest.isExistingUser();

        if (!existingUser) {
            String name = createUserRequest.getName();
            String accountType = createUserRequest.getAccountType();
            String phone = String.valueOf(createUserRequest.getPhone());
            String email = createUserRequest.getEmail();

            if (name == null || name.isEmpty() ||
                    accountType == null || accountType.isEmpty() ||
                    (phone == null || phone.isEmpty()) && (email == null || email.isEmpty())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please enter all required fields: name, account type, and either phone or email.");
            }

            if (!accountType.equalsIgnoreCase("savings") && !accountType.equalsIgnoreCase("current")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid account type. Please choose either 'savings' or 'current'.");
            }

            Long phoneNumber = null;
            if (phone != null && !phone.isEmpty()) {
                try {
                    phoneNumber = Long.parseLong(phone);
                    if (phoneNumber == 0) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid phone number format.");
                    }
                } catch (NumberFormatException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid phone number format.");
                }
            }

            BankUser user = service.createUser(name, accountType);
            user.setMobileNumber(phoneNumber);
            user.setEmail(email);
            service.saveUser(user);
            LOGGER.log(Level.INFO, "Created new user with name " + name + " and account type " + accountType + " successfully.");
            ApiResponse<Integer> apiResponse  = new ApiResponse<>("Created new user with name " + name + " and account type " + accountType + " successfully.",user.get());
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } else {
            String accountType = createUserRequest.getAccountType();
            int userId = createUserRequest.getExistingUserId();

            if (!accountType.equalsIgnoreCase("savings") && !accountType.equalsIgnoreCase("current")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid account type. Please choose either 'savings' or 'current'.");
            }

            Optional<BankUser> user = service.getUserById(userId);
            if (user.isPresent()) {
                user.get().addAccount(accountType);
                service.saveUser(user.get());
                LOGGER.log(Level.INFO, "Added new account type " + accountType + " for existing user ID " + userId + " successfully.");
                return ResponseEntity.ok(user.get());
            } else {
                logNotFound("User with ID " + userId + " not found.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/user/{userId}/accounts/{accountId}/updateAddress")
    public ResponseEntity<ApiResponse<BankUser>> updateAddress(@PathVariable int userId, @PathVariable Long accountId, @RequestBody UpdateAddress updateAddress) {
        Address address = updateAddress.getAddress();
        System.out.println(address);
        Optional<BankUser> user = service.getUserById(userId);
        if (user.isPresent()) {
            user.get().setAddress(address);
            service.saveUser(user.get());
            LOGGER.log(Level.INFO, "Updated address for user ID " + userId + " and account ID " + accountId + " successfully.");
            ApiResponse<BankUser> apiResponse = new ApiResponse<>("Updated address for user ID " + userId + " and account ID " + accountId + " successfully with Address : " + address.toString() ,user.get());
            return ResponseEntity.ok(apiResponse);
        } else {
            logNotFound("User with ID " + userId + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/user/{userId}/accounts/{accountId}/updatePhone")
    public ResponseEntity<ApiResponse<BankUser>> updatePhone(@PathVariable int userId, @PathVariable Long accountId, @RequestBody UpdatePhone updatePhone) {
        Long phone = updatePhone.getPhone();
        Optional<BankUser> user = service.getUserById(userId);
        if (user.isPresent()) {
            user.get().setMobileNumber(phone);
            service.saveUser(user.get());
            LOGGER.log(Level.INFO, "Updated phone number for user ID " + userId + " and account ID " + accountId + " successfully.");
            ApiResponse<BankUser> apiResponse = new ApiResponse<>("Updated phone number for user ID " + userId + " and account ID " + accountId + " successfully with phone : " + phone.toString() ,user.get());
            return ResponseEntity.ok(apiResponse);
        } else {
            logNotFound("User with ID " + userId + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/user/{userId}/accounts/{accountId}/updateEmail")
    public ResponseEntity<ApiResponse<BankUser>> updateEmail(@PathVariable int userId, @PathVariable Long accountId, @RequestBody UpdateEmail updateEmail) {
        String email = updateEmail.getEmail();
        Optional<BankUser> user = service.getUserById(userId);
        if (user.isPresent()) {
            user.get().setEmail(email);
            service.saveUser(user.get());
            LOGGER.log(Level.INFO, "Updated email for user ID " + userId + " and account ID " + accountId + " successfully.");
            ApiResponse<BankUser> apiResponse = new ApiResponse<>("Updated email for user ID " + userId + " and account ID " + accountId + " successfully with email : " + email,user.get());
            return ResponseEntity.ok(apiResponse);
        } else {
            logNotFound("User with ID " + userId + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}/accounts/{accountId}/balance")
    public ResponseEntity<ApiResponse<BankUser>> updateUserBalance(@PathVariable int id, @PathVariable Long accountId, @RequestBody BalanceUpdateRequest balanceUpdateRequest) {
        String newBalance = String.valueOf(balanceUpdateRequest.getNewBalance());
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            service.addBalance(user.get(), Integer.parseInt(newBalance), accountId);
            LOGGER.log(Level.INFO, "Updated balance for user ID " + id + " and account ID " + accountId + " successfully.");
            ApiResponse<BankUser> apiResponse = new ApiResponse<>("Updated balance for user ID " + id + " and account ID " + accountId + " successfully.",user.get());
            return ResponseEntity.ok(apiResponse);
        } else {
            logNotFound("User with ID " + id + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/user/{id}/accounts/{accountId}/balance/add")
    public ResponseEntity<ApiResponse<BankUser>> addBalance(@PathVariable int id, @PathVariable Long accountId, @RequestBody AddBalance addBalance) {
        int balanceToAdd = addBalance.getBalanceToAdd();
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            boolean done = service.addBalance(user.get(), balanceToAdd, accountId);
            if (done) {
                LOGGER.log(Level.INFO, "Added balance to user ID " + id + " and account ID " + accountId + " successfully.");
                ApiResponse<BankUser> apiResponse = new ApiResponse<>("Added balance to user ID " + id + " and account ID " + accountId + " successfully.",user.get());
                return ResponseEntity.ok(apiResponse);
            } else {
                logError("Unable to add balance to account ID " + accountId + ".");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            logNotFound("User with ID " + id + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/user/{id}/accounts/{accountId}/balance/remove")
    public ResponseEntity<RemoveResponse> removeBalance(@PathVariable int id, @PathVariable Long accountId, @RequestBody RemoveBalance removeBalance) {
        int balanceToRemove = removeBalance.getBalanceToRemove();
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            boolean done = service.removeBalance(user.get(), balanceToRemove, accountId);
            if (done) {
                RemoveResponse removeResponse = new RemoveResponse("Removed balance for user ID " + id + " and account ID " + accountId + " successfully.", user.get());
                LOGGER.log(Level.INFO, "Removed balance for user ID " + id + " and account ID " + accountId + " successfully.");
                return ResponseEntity.ok(removeResponse);
            } else {
                logError("Unable to remove balance from account ID " + accountId + ".");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            logNotFound("User with ID " + id + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/user/{id}/balance/transfer")
    public ResponseEntity<TransferResponse> transferBalance(@PathVariable int id, @RequestBody TransferRequest transferRequest) {
        int balanceToTransfer = transferRequest.getBalanceToTransfer();
        int userToTransfer = transferRequest.getUserIdTO();
        Long fromAccountId = transferRequest.getAccountIdFrom();
        Long toAccountId = transferRequest.getAccountIdTo();

        Optional<BankUser> fromUser = service.getUserById(id);
        Optional<BankUser> toUser = service.getUserById(userToTransfer);
        if (fromUser.isPresent() && toUser.isPresent()) {
            boolean done = service.transferBetweenTwoAccounts(fromUser.get(), toUser.get(), fromAccountId, toAccountId, balanceToTransfer);
            if (done) {
                TransferResponse response = new TransferResponse("Transferred balance from user ID " + id + " to user ID " + userToTransfer + " successfully.", fromUser.get());
                LOGGER.log(Level.INFO, "Transferred balance from user ID " + id + " to user ID " + userToTransfer + " successfully.");
                return ResponseEntity.ok(response);
            } else {
                logError("Transfer from account ID " + fromAccountId + " to account ID " + toAccountId + " failed.");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            logNotFound("User or account not found for transfer.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable int id) {
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            service.deleteUser(id);
            LOGGER.log(Level.INFO, "Deleted user with ID " + id + " successfully.");
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            logNotFound("User with ID " + id + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrResponse> handleAllExceptions(Exception ex, WebRequest request) {
        LOGGER.log(Level.SEVERE, "An error occurred: ", ex);
        ErrResponse errorResponse = new ErrResponse(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logNotFound(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    private void logError(String message) {
        LOGGER.log(Level.SEVERE, message);
    }
}