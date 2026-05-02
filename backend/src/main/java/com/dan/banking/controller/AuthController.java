package com.dan.banking.controller;

import com.dan.banking.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dan.banking.dto.LoginRequest;
import com.dan.banking.service.IAuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

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
