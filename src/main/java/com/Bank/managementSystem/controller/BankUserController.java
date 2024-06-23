package com.Bank.managementSystem.controller;
import com.Bank.managementSystem.DTO.*;
import com.Bank.managementSystem.Response.*;
import com.Bank.managementSystem.entity.Address;
import com.Bank.managementSystem.entity.BankUser;
import com.Bank.managementSystem.service.BankingServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/users")
public class BankUserController {

    @Autowired
    private BankingServices service;

    private static final Logger LOGGER = Logger.getLogger(BankUserController.class.getName());

    @GetMapping("/")
    public ResponseEntity<List<BankUser>> getUsers() {
        List<BankUser> all = service.getAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankUser> getUserById(@PathVariable int id) {
        Optional<BankUser> user = service.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logAndReturnNotFound("User with ID " + id + " not found.");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @GetMapping("/{id}/accounts/{accountId}/balance")
    public ResponseEntity<GetBalanceResponse> getUserBalance(@PathVariable int id, @PathVariable Long accountId) {
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            String balance = user.get().getAccountBalance(accountId);
            if (balance != null) {
                GetBalanceResponse getBalanceResponse = new GetBalanceResponse("User balance : " + balance, user.get());
                return ResponseEntity.ok(getBalanceResponse);
            } else {
                logAndReturnNotFound("Balance not found for account ID " + accountId + ".");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            logAndReturnNotFound("User with ID " + id + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/accounts/{accountId}/transactions")
    public ResponseEntity<List<String>> getTransactionHistory(@PathVariable int id, @PathVariable Long accountId) {
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            List<String> transactions = service.getTransactionHistory(user.get(), accountId);
            if (transactions != null) {
                return ResponseEntity.ok(transactions);
            } else {
                logAndReturnNotFound("Transactions not found for account ID " + accountId + ".");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            logAndReturnNotFound("User with ID " + id + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    public ResponseEntity<BankUser> addUser(CreateUserRequest createUserRequest) {
        boolean existingUser = createUserRequest.isExistingUser();
        if (!existingUser) {
            String name = createUserRequest.getName();
            String accountType = createUserRequest.getAccountType();
            BankUser user = service.createUser(name, accountType);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } else {
            String accountType = createUserRequest.getAccountType();
            int userId = createUserRequest.getExistingUserId();
            Optional<BankUser> user = service.getUserById(userId);
            if (user.isPresent()) {
                user.get().addAccount(accountType);
                service.saveUser(user.get());
                return ResponseEntity.ok(user.get());
            } else {
                logAndReturnNotFound("User with ID " + userId + " not found.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    }

    @PostMapping("/{userId}/accounts/{accountId}/updateAddress")
    public ResponseEntity<BankUser> updateAddress(@PathVariable int userId, @PathVariable Long accountId, @RequestBody UpdateAddress updateAddress) {
        Address address = updateAddress.getAddress();
        Optional<BankUser> user = service.getUserById(userId);
        if (user.isPresent()) {
            user.get().setAddress(address);
            service.saveUser(user.get());
            return ResponseEntity.ok(user.get());
        } else {
            logAndReturnNotFound("User with ID " + userId + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{userId}/accounts/{accountId}/updatePhone")
    public ResponseEntity<BankUser> updatePhone(@PathVariable int userId, @PathVariable Long accountId, @RequestBody UpdatePhone updatePhone) {
        Long phone = updatePhone.getPhone();
        Optional<BankUser> user = service.getUserById(userId);
        if (user.isPresent()) {
            user.get().setMobileNumber(phone);
            service.saveUser(user.get());
            return ResponseEntity.ok(user.get());
        } else {
            logAndReturnNotFound("User with ID " + userId + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{userId}/accounts/{accountId}/updateEmail")
    public ResponseEntity<BankUser> updateEmail(@PathVariable int userId, @PathVariable Long accountId, @RequestBody UpdateEmail updateEmail) {
        String email = updateEmail.getEmail();
        Optional<BankUser> user = service.getUserById(userId);
        if (user.isPresent()) {
            user.get().setEmail(email);
            service.saveUser(user.get());
            return ResponseEntity.ok(user.get());
        } else {
            logAndReturnNotFound("User with ID " + userId + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/accounts/{accountId}/balance")
    public ResponseEntity<String> updateUserBalance(@PathVariable int id, @PathVariable Long accountId, @RequestBody BalanceUpdateRequest balanceUpdateRequest) {
        String newBalance = String.valueOf(balanceUpdateRequest.getNewBalance());
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            service.addBalance(user.get(), Integer.parseInt(newBalance), accountId);
            return ResponseEntity.ok("User balance updated successfully.");
        } else {
            logAndReturnNotFound("User with ID " + id + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
            } else {
                logAndReturnError("Unable to add balance to account ID " + accountId + ".");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            logAndReturnNotFound("User with ID " + id + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
            } else {
                logAndReturnError("Unable to remove balance from account ID " + accountId + ".");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            logAndReturnNotFound("User with ID " + id + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/balance/transfer")
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
                TransferResponse response = new TransferResponse("Transfer completed successfully.", fromUser.get());
                return ResponseEntity.ok(response);
            } else {
                logAndReturnError("Transfer from account ID " + fromAccountId + " to account ID " + toAccountId + " failed.");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            logAndReturnNotFound("User or account not found for transfer.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable int id) {
        Optional<BankUser> user = service.getUserById(id);
        if (user.isPresent()) {
            service.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            logAndReturnNotFound("User with ID " + id + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrResponse> handleAllExceptions(Exception ex, WebRequest request) {
        LOGGER.log(Level.SEVERE, "An error occurred: ", ex);
        ErrResponse errorResponse = new ErrResponse(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logAndReturnNotFound(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    private void logAndReturnError(String message) {
        LOGGER.log(Level.SEVERE, message);
    }
}

