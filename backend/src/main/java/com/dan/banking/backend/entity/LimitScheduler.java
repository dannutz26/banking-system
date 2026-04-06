package com.dan.banking.backend.entity;

import com.dan.banking.backend.repository.SpendingLimitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LimitScheduler {
    private final SpendingLimitRepository limitRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetLimits() {
        LocalDateTime now = LocalDateTime.now();
        List<SpendingLimit> allLimits = limitRepository.findAll();

        for (SpendingLimit limit : allLimits) {
            if (shouldReset(limit, now)) {
                limit.setCurrentSpent(BigDecimal.ZERO);
                limit.setLastReset(now);
                limitRepository.save(limit);
            }
        }
    }

    private boolean shouldReset(SpendingLimit limit, LocalDateTime now) {
        return switch (limit.getPeriod()) {
            case DAILY -> true;
            case WEEKLY -> now.getDayOfWeek() == DayOfWeek.MONDAY;
            case MONTHLY -> now.getDayOfMonth() == 1;
        };
    }
}