package org.example.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.dtos.LoginDTO;
import org.example.dtos.UserRegistrationDTO;
import org.example.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    /**
     Registers a new user with the provided user registration data.
     @param userDTO the user registration data transfer object
     @return a response entity with a success message
     **/
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationDTO userDTO) {
        userService.registerNewUser(userDTO);
        return ResponseEntity.ok("User registered successfully!");
    }

    /**
     Authenticates a user and returns a JWT token if successful.
     @param loginDTO the login data transfer object containing username and password
     @return a map containing the JWT token and a success message
     **/
    @PostMapping("/login")
    public Map<String, Object> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        return userService.loginUser(loginDTO);
    }

    /**
     Logs out a user.
     @return a response entity with a success message
     **/
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        // Invalidate jwt token here
        return ResponseEntity.ok("User logged out successfully!");
    }
}
