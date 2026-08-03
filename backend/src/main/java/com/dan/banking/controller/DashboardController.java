package com.dan.banking.controller;

import com.dan.banking.dto.*;
import com.dan.banking.entity.*;
import com.dan.banking.service.IAccountService;
import com.dan.banking.service.CardService;
import com.dan.banking.service.CurrencyService;
import com.dan.banking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final UserService userService;
    private final IAccountService accountService;
    private final CurrencyService currencyService;
    private final CardService cardService;

    @GetMapping("/user-data")
    public ResponseEntity<UserDashboardResponse> getUserData(Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email).orElseThrow();
        UserSettings settings = user.getSettings();
        List<Account> accounts = accountService.getAccounts(email);

        String targetCurrency = (settings != null && settings.getPreferredCurrency() != null && !settings.getPreferredCurrency().isEmpty()) 
                ? settings.getPreferredCurrency() 
                : "EUR";
        double totalNetWorth = accounts.stream()
                .mapToDouble(acc -> {
                    if (acc.getCurrency().equals(targetCurrency)) {
                        return acc.getBalance();
                    }
                    return acc.getBalance() * currencyService.getExchangeRate(acc.getCurrency(), targetCurrency);
                })
                .sum();

        return ResponseEntity.ok(new UserDashboardResponse(
                user.getFirstName(),
                totalNetWorth,
                targetCurrency,
                settings != null ? settings.getPrimaryAccountIban() : null
        ));
    }

    @GetMapping("/user-accounts")
    public ResponseEntity<List<AccountResponse>> getUserAccounts(Principal principal) {
        String email = principal.getName();
        List<Account> accountList = accountService.getAccounts(email);

        List<AccountResponse> response = accountList.stream()
                .map(acc -> new AccountResponse(acc.getId(), acc.getIban(), acc.getBalance(), acc.getCurrency()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-account")
    public ResponseEntity<String> createAccount(@Valid @RequestBody AccountRequest accountRequest, Principal principal) {
        try {
            accountRequest.setOwnerEmail(principal.getName());
            accountService.insertAccount(accountRequest);
            return ResponseEntity.ok("Account created successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Account creation failed: " + e.getMessage());
        }
    }

    @GetMapping("/currencies")
    public ResponseEntity<List<Map<String, String>>> getAvailableCurrencies() {
        return ResponseEntity.ok(currencyService.getAvailableCurrencies());
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@Valid @RequestBody TransferRequest request, 
                                                @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                                                Principal principal) {
        try {
            request.setOwnerEmail(principal.getName());
            accountService.transferMoney(request, idempotencyKey);
            return ResponseEntity.ok("Transfer completed successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(Principal principal) {
        String email = principal.getName();
        List<Transaction> transactions = accountService.getAllTransactionsForUser(email);
        List<TransactionResponse> response = transactions.stream()
                .map(t -> new TransactionResponse(t.getId(), t.getSourceIban(), t.getTargetIban(), t.getAmount(), t.getCurrency(), t.getDescription(), t.getTimestamp()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> depositMoney(@Valid @RequestBody DepositRequest request) {
        try {
            accountService.depositMoney(request.getIban(), request.getAmount());
            return ResponseEntity.ok("Deposit completed successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/cards")
    public ResponseEntity<List<CardResponse>> getCards(Principal principal) {
        List<CardResponse> cards = cardService.getCardsByEmail(principal.getName()).stream()
                .map(this::toCardResponse)
                .toList();
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/cards/create")
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardRequest request, Principal principal) {
        Card createdCard = cardService.createCard(principal.getName(), request.getIban(), request.getType(), request.isDisposable());
        return ResponseEntity.ok(toCardResponse(createdCard));
    }

    private CardResponse toCardResponse(Card card) {
        String fullNumber = card.getCardNumber();
        String maskedNumber = fullNumber;
        if (maskedNumber != null && maskedNumber.length() >= 4) {
            maskedNumber = "**** **** **** " + maskedNumber.substring(maskedNumber.length() - 4);
        }
        return new CardResponse(
                card.getId(),
                maskedNumber,
                card.getCardHolderName(),
                card.getExpiryDate(),
                card.getCardType(),
                card.isBlocked(),
                card.isDisposable(),
                card.getAccountId()
        );
    }

    @PostMapping("/cards/toggle-block")
    public ResponseEntity<String> toggleBlock(@RequestParam Long cardId, Principal principal) {
        cardService.toggleBlock(cardId, principal.getName());
        return ResponseEntity.ok("Success");
    }

    @DeleteMapping("/cards/delete")
    public ResponseEntity<String> deleteCard(@RequestParam Long cardId, Principal principal) {
        try {
            cardService.deleteCard(cardId, principal.getName());
            return ResponseEntity.ok("Card cancelled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
