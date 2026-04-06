package com.dan.banking.backend.service;

import com.dan.banking.backend.entity.Account;
import com.dan.banking.backend.entity.Card;
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

    public List<SpendingLimit> getLimits(Long id, String type) {
        return type.equalsIgnoreCase("ACCOUNT")
                ? limitRepository.findByAccountId(id)
                : limitRepository.findByCardId(id);
    }

    @Transactional
    public void setAccountLimit(Account account, LimitPeriod period, BigDecimal amount) {
        SpendingLimit limit = limitRepository.findByAccountIdAndPeriod(account.getId(), period)
                .orElse(new SpendingLimit());
        limit.setAccount(account);
        saveLimit(limit, period, amount);
    }

    @Transactional
    public void setCardLimit(Card card, LimitPeriod period, BigDecimal amount) {
        SpendingLimit limit = limitRepository.findByCardIdAndPeriod(card.getId(), period)
                .orElse(new SpendingLimit());
        limit.setCard(card);
        saveLimit(limit, period, amount);
    }

    private void saveLimit(SpendingLimit limit, LimitPeriod period, BigDecimal amount) {
        limit.setPeriod(period);
        limit.setLimitAmount(amount);
        if (limit.getId() == null) {
            limit.setCurrentSpent(BigDecimal.ZERO);
            limit.setLastReset(LocalDateTime.now());
        }
        limitRepository.save(limit);
    }

    @Transactional
    public void deleteLimits(Long id, String type) {
        List<SpendingLimit> limits = getLimits(id, type);
        limitRepository.deleteAll(limits);
    }

    public void validateTransaction(Long accountId, Long cardId, BigDecimal amount) {
        if (accountId != null) check(limitRepository.findByAccountId(accountId), amount);
        if (cardId != null) check(limitRepository.findByCardId(cardId), amount);
    }

    private void check(List<SpendingLimit> limits, BigDecimal amount) {
        for (SpendingLimit limit : limits) {
            if (limit.getLimitAmount() == null) continue;
            if (limit.getCurrentSpent().add(amount).compareTo(limit.getLimitAmount()) > 0) {
                throw new RuntimeException(limit.getPeriod() + " limit exceeded.");
            }
        }
    }

    @Transactional
    public void updateSpentCounters(Long accountId, Long cardId, BigDecimal amount) {
        if (accountId != null) {
            limitRepository.findByAccountId(accountId).forEach(l -> l.setCurrentSpent(l.getCurrentSpent().add(amount)));
        }
        if (cardId != null) {
            limitRepository.findByCardId(cardId).forEach(l -> l.setCurrentSpent(l.getCurrentSpent().add(amount)));
        }
    }
}