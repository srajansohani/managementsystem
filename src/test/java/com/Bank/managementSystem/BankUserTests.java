package com.Bank.managementSystem;
import com.Bank.managementSystem.DTO.*;
import com.Bank.managementSystem.Response.*;
import com.Bank.managementSystem.controller.BankUserController;
import com.Bank.managementSystem.entity.Address;
import com.Bank.managementSystem.entity.BankUser;
import com.Bank.managementSystem.service.BankingServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BankUserTests {

    @Mock
    private BankingServices bankingServices;

    @InjectMocks
    private BankUserController bankUserController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUsers() {
        List<BankUser> userList = new ArrayList<>();
        userList.add(new BankUser());
        when(bankingServices.getAll()).thenReturn(userList);

        ResponseEntity<List<BankUser>> response = bankUserController.getUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(bankingServices, times(1)).getAll();
    }

    @Test
    void testGetUserById() {
        int userId = 1;
        BankUser user = new BankUser();
        when(bankingServices.getUserById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = bankUserController.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, ((ApiResponse<?>) response.getBody()).getData());
        verify(bankingServices, times(1)).getUserById(userId);
    }

    @Test
    void testGetUserByIdNotFound() {
        int userId = 1;
        when(bankingServices.getUserById(userId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = bankUserController.getUserById(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(bankingServices, times(1)).getUserById(userId);
    }

    @Test
    void testAddUser() {
        CreateUserRequest request = new CreateUserRequest();
        request.setExistingUser(false);
        request.setName("John Doe");
        request.setAccountType("savings");
        request.setPhone(Long.parseLong("1234567890"));

        BankUser user = new BankUser();
        when(bankingServices.createUser(anyString(), anyString())).thenReturn(user);
        when(bankingServices.saveUser(any(BankUser.class))).thenReturn(user);

        ResponseEntity<?> response = bankUserController.addUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(bankingServices, times(1)).createUser(anyString(), anyString());
        verify(bankingServices, times(1)).saveUser(any(BankUser.class));
    }

    @Test
    void testAddExistingUserAccount() {
        CreateUserRequest request = new CreateUserRequest();
        request.setExistingUser(true);
        request.setExistingUserId(1);
        request.setAccountType("current");

        BankUser user = new BankUser();
        when(bankingServices.getUserById(1)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = bankUserController.addUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bankingServices, times(1)).getUserById(1);
        verify(bankingServices, times(1)).saveUser(any(BankUser.class));
    }

    @Test
    void testUpdateAddress() {
        int userId = 1;
        long accountId = 1L;
        UpdateAddress updateAddress = new UpdateAddress();
        Address address = new Address();
        updateAddress.setAddress(address);

        BankUser user = new BankUser();
        when(bankingServices.getUserById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse<BankUser>> response = bankUserController.updateAddress(userId, accountId, updateAddress);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bankingServices, times(1)).getUserById(userId);
        verify(bankingServices, times(1)).saveUser(any(BankUser.class));
    }
}

