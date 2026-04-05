package com.dan.banking.backend.controller;

import com.dan.banking.backend.dto.AccountRequest;
import com.dan.banking.backend.dto.AccountResponse;
import com.dan.banking.backend.dto.TransferRequest;
import com.dan.banking.backend.dto.UserResponse;
import com.dan.banking.backend.entity.*;
import com.dan.banking.backend.service.AccountService;
import com.dan.banking.backend.service.CardService;
import com.dan.banking.backend.service.CurrencyService;
import com.dan.banking.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final UserService userService;
    private final AccountService accountService;
    private final CurrencyService currencyService;
    private final CardService cardService;

    @GetMapping("/user-data")
    public ResponseEntity<?> getUserData(@RequestParam String email) {
        User user = userService.getUserByEmail(email).orElseThrow();
        UserSettings settings = user.getSettings();
        List<Account> accounts = accountService.getAccounts(email);

        String targetCurr = (settings != null) ? settings.getPreferredCurrency() : "EUR";

        double totalNetWorth = accounts.stream()
                .mapToDouble(acc -> {
                    if (acc.getCurrency().equals(targetCurr)) return acc.getBalance();
                    return acc.getBalance() * currencyService.getExchangeRate(acc.getCurrency(), targetCurr);
                })
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("firstName", user.getFirstName());
        response.put("totalNetWorth", totalNetWorth);
        response.put("preferredCurrency", targetCurr);
        response.put("primaryIban", settings != null ? settings.getPrimaryAccountIban() : null);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-accounts")
    public ResponseEntity<?> getUserAccounts(@RequestParam String email) {
        List<Account> accountList = accountService.getAccounts(email);

        List<AccountResponse> response = accountList.stream()
                .map(acc -> new AccountResponse(acc.getId(), acc.getIban(), acc.getBalance(), acc.getCurrency()))
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

    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(@RequestBody TransferRequest request) {
        try {
            accountService.transferMoney(request);
            return ResponseEntity.ok("Transfer completed successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(@RequestParam String email) {
        try {
            List<Transaction> transactions = accountService.getAllTransactionsForUser(email);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Could not fetch transactions");
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> depositMoney(@RequestBody com.dan.banking.backend.dto.DepositRequest request) {
        try {
            accountService.depositMoney(request.getIban(), request.getAmount());
            return ResponseEntity.ok("Deposit completed successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/cards")
    public ResponseEntity<?> getCards(@RequestParam String email) {
        return ResponseEntity.ok(cardService.getCardsByEmail(email));
    }

    @PostMapping("/cards/create")
    public ResponseEntity<?> createCard(@RequestParam String iban, @RequestParam String type, @RequestParam boolean disposable) {
        try {
            return ResponseEntity.ok(cardService.createCard(iban, type, disposable));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/cards/toggle-block")
    public ResponseEntity<?> toggleBlock(@RequestParam Long cardId) {
        cardService.toggleBlock(cardId);
        return ResponseEntity.ok("Success");
    }

    @DeleteMapping("/cards/delete")
    public ResponseEntity<?> deleteCard(@RequestParam Long cardId) {
        try {
            cardService.deleteCard(cardId);
            return ResponseEntity.ok("Card cancelled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
