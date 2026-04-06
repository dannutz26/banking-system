package com.dan.banking.backend.service;

import com.dan.banking.backend.entity.Account;
import com.dan.banking.backend.entity.SpendingLimit;
import com.dan.banking.backend.entity.LimitPeriod;
import com.dan.banking.backend.repository.SpendingLimitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpendingLimitService {

    private final SpendingLimitRepository limitRepository;

    @Transactional
    public void setLimit(Account account, LimitPeriod period, BigDecimal limitAmount) {
        SpendingLimit limit = limitRepository.findByAccountIdAndPeriod(account.getId(), period)
                .orElse(new SpendingLimit());

        limit.setAccount(account);
        limit.setPeriod(period);
        limit.setLimitAmount(limitAmount);

        if (limit.getId() == null) {
            limit.setCurrentSpent(BigDecimal.ZERO);
            limit.setLastReset(LocalDateTime.now());
        }

        limitRepository.save(limit);
    }

    public void validateTransaction(Long accountId, BigDecimal amount) {
        List<SpendingLimit> limits = limitRepository.findByAccountId(accountId);

        for (SpendingLimit limit : limits) {
            if (limit.getLimitAmount() == null) continue;

            BigDecimal projectedTotal = limit.getCurrentSpent().add(amount);

            if (projectedTotal.compareTo(limit.getLimitAmount()) > 0) {
                throw new RuntimeException(String.format(
                        "%s spending limit exceeded. Remaining: %s",
                        limit.getPeriod(),
                        limit.getLimitAmount().subtract(limit.getCurrentSpent())
                ));
            }
        }
    }

    @Transactional
    public void updateSpentCounters(Long accountId, BigDecimal amount) {
        List<SpendingLimit> limits = limitRepository.findByAccountId(accountId);
        for (SpendingLimit limit : limits) {
            limit.setCurrentSpent(limit.getCurrentSpent().add(amount));
            limitRepository.save(limit);
        }
    }
}