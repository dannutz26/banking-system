package com.dan.banking.backend.controller;

import com.dan.banking.backend.entity.Account;
import com.dan.banking.backend.entity.Card;
import com.dan.banking.backend.entity.LimitPeriod;
import com.dan.banking.backend.entity.SpendingLimit;
import com.dan.banking.backend.repository.AccountRepository;
import com.dan.banking.backend.repository.CardRepository;
import com.dan.banking.backend.service.SpendingLimitService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/limits")
@RequiredArgsConstructor
public class SpendingLimitController {

    private final SpendingLimitService limitService;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

    @GetMapping("/{type}s/{id}")
    public ResponseEntity<List<SpendingLimit>> getLimits(@PathVariable String type, @PathVariable Long id) {
        return ResponseEntity.ok(limitService.getLimits(id, type));
    }

    @PostMapping("/accounts/{id}")
    public ResponseEntity<?> setAccountLimit(@PathVariable Long id, @RequestBody LimitRequest req) {
        Account account = accountRepository.findById(id).orElseThrow();
        limitService.setAccountLimit(account, req.getPeriod(), req.getLimitAmount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cards/{id}")
    public ResponseEntity<?> setCardLimit(@PathVariable Long id, @RequestBody LimitRequest req) {
        Card card = cardRepository.findById(id).orElseThrow();
        limitService.setCardLimit(card, req.getPeriod(), req.getLimitAmount());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{type}s/{id}")
    public ResponseEntity<?> deleteLimits(@PathVariable String type, @PathVariable Long id) {
        limitService.deleteLimits(id, type);
        return ResponseEntity.ok().build();
    }

    @Data
    static class LimitRequest {
        private LimitPeriod period;
        private BigDecimal limitAmount;
    }
}