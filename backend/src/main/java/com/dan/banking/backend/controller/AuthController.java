package com.dan.banking.backend.controller;

import com.dan.banking.backend.dto.RegisterRequest;
import com.dan.banking.backend.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dan.banking.backend.dto.LoginRequest;
import com.dan.banking.backend.service.AuthService;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,
                                   @RequestParam(defaultValue = "false") boolean rememberMe,
                                   HttpServletResponse response) {
        if (authService.verifyLogin(loginRequest)) {
            if (rememberMe) {
                authService.createRememberMeToken(loginRequest.getEmail(), response);
            }
            return ResponseEntity.ok(loginRequest.getEmail());
        }
        return ResponseEntity.status(401).body("Invalid email or password");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        authService.registerUser(registerRequest);
        return ResponseEntity.ok("Registration successful");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(name = "remember-me", required = false) String rememberMeCookie,
                                            HttpServletResponse response) {
        if (rememberMeCookie == null) {
            return ResponseEntity.status(401).body("No session found");
        }

        return authService.processAutoLogin(rememberMeCookie, response)
                .map(email -> ResponseEntity.ok(email))
                .orElse(ResponseEntity.status(401).body("Invalid or expired session"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "remember-me", required = false) String rememberMeCookie,
                                    HttpServletResponse response) {
        authService.logout(rememberMeCookie, response);
        return ResponseEntity.ok("Session cleared successfully");
    }
}