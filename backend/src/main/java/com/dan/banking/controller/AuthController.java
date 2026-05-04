package com.dan.banking.controller;

import com.dan.banking.dto.AuthResponse;
import com.dan.banking.dto.LoginRequest;
import com.dan.banking.dto.RegisterRequest;
import com.dan.banking.dto.UserResponse;
import com.dan.banking.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        if (!authService.verifyLogin(loginRequest)) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        String token = authService.generateToken(loginRequest.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, loginRequest.getEmail()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.registerUser(registerRequest);
        return ResponseEntity.ok("Registration successful");
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(authService.getCurrentUser(principal.getName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout succeeded");
    }
}
