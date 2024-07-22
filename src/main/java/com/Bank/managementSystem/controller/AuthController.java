package com.Bank.managementSystem.controller;
import com.Bank.managementSystem.DTO.*;
import com.Bank.managementSystem.Response.AuthResponse;
import com.Bank.managementSystem.Security.AdminConfig;
import com.Bank.managementSystem.entity.User;
import com.Bank.managementSystem.Security.JwtTokenUtil;
import com.Bank.managementSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private AdminConfig adminConfig;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserService userService;


    public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        User newUser = new User();
        newUser.setUsername(userRegistrationRequest.getUsername());
        newUser.setPassword(userRegistrationRequest.getPassword());

        if (adminConfig.getAdminUsername().equals(userRegistrationRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Cannot register as admin");
        } else {
            newUser.setRole("ROLE_USER");
        }
        userService.saveUser(newUser);
        return ResponseEntity.ok("User registered successfully");
    }

   //Uncomment to come back to JWT based Authentication
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        System.out.println("Username: " + authRequest.getUsername());
        System.out.println("Password: " + authRequest.getPassword());
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            final String jwt = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(jwt));
        } catch (BadCredentialsException e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }
}