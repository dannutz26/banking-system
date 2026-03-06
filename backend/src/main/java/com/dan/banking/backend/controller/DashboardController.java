package com.dan.banking.backend.controller;

import com.dan.banking.backend.dto.AccountRequest;
import com.dan.banking.backend.dto.AccountResponse;
import com.dan.banking.backend.dto.UserResponse;
import com.dan.banking.backend.entity.Account;
import com.dan.banking.backend.entity.User;
import com.dan.banking.backend.service.AccountService;
import com.dan.banking.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final UserService userService;
    private final AccountService accountService;

    @GetMapping("/user-data")
    public ResponseEntity<?> getUserData(@RequestParam String email) {
        Optional<User> userOpt = userService.getUserByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserResponse response = new com.dan.banking.backend.dto.UserResponse(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @GetMapping("/user-accounts")
    public ResponseEntity<?> getUserAccounts(@RequestParam String email) {
        List<Account> accountList = accountService.getAccounts(email);

        List<AccountResponse> response = accountList.stream()
                .map(acc -> new AccountResponse(acc.getIban(), acc.getBalance(), acc.getCurrency()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestBody AccountRequest accountRequest) {
        try {
            accountService.insertAccount(accountRequest);
            return ResponseEntity.ok("Account created successfully!");
        }  catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Account creation failed!");
        }
    }

    @GetMapping("/currencies")
    public ResponseEntity<List<Map<String, String>>> getAvailableCurrencies() {
        List<Map<String, String>> currencies = Currency.getAvailableCurrencies().stream()
                .map(c -> {
                    Map<String, String> map = new java.util.HashMap<>();
                    map.put("code", c.getCurrencyCode());
                    map.put("name", c.getDisplayName());
                    return map;
                })
                .sorted((a, b) -> a.get("code").compareTo(b.get("code")))
                .toList();

        return ResponseEntity.ok(currencies);
    }
}
