package com.dan.banking.config;

import com.dan.banking.entity.ExchangeRate;
import com.dan.banking.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final ExchangeRateRepository exchangeRateRepository;

    @Override
    public void run(String... args) {
        if (exchangeRateRepository.count() == 0) {
            System.out.println("Seeding initial exchange rates...");
            
            List<ExchangeRate> defaultRates = List.of(
                    new ExchangeRate(null, "USD", "EUR", 0.92, LocalDateTime.now()),
                    new ExchangeRate(null, "USD", "GBP", 0.79, LocalDateTime.now()),
                    new ExchangeRate(null, "USD", "RON", 4.58, LocalDateTime.now()),
                    new ExchangeRate(null, "USD", "JPY", 150.0, LocalDateTime.now()),
                    new ExchangeRate(null, "USD", "AUD", 1.53, LocalDateTime.now()),
                    new ExchangeRate(null, "USD", "CAD", 1.35, LocalDateTime.now()),
                    new ExchangeRate(null, "USD", "CHF", 0.90, LocalDateTime.now()),
                    new ExchangeRate(null, "USD", "CNY", 7.23, LocalDateTime.now()),
                    new ExchangeRate(null, "USD", "SEK", 10.45, LocalDateTime.now()),
                    new ExchangeRate(null, "USD", "NZD", 1.66, LocalDateTime.now())
            );

            exchangeRateRepository.saveAll(defaultRates);
            System.out.println("Successfully seeded " + defaultRates.size() + " exchange rates relative to USD.");
        }
    }
}
