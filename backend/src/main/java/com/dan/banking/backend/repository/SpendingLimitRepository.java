package com.dan.banking.backend.repository;

import com.dan.banking.backend.entity.SpendingLimit;
import com.dan.banking.backend.entity.LimitPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpendingLimitRepository extends JpaRepository<SpendingLimit, Long> {
    List<SpendingLimit> findByAccountId(Long accountId);
    Optional<SpendingLimit> findByAccountIdAndPeriod(Long accountId, LimitPeriod period);

    List<SpendingLimit> findByCardId(Long cardId);
    Optional<SpendingLimit> findByCardIdAndPeriod(Long cardId, LimitPeriod period);
}