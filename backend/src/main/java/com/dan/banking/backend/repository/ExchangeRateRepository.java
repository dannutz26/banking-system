package com.dan.banking.backend.repository;

import com.dan.banking.backend.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findByToCurrency(String toCurrency);
    boolean existsByToCurrency(String toCurrency);
}