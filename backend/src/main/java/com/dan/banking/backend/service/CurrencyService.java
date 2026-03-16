package com.dan.banking.backend.service;

import com.dan.banking.backend.entity.ExchangeRate;
import com.dan.banking.backend.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final ExchangeRateRepository rateRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${currencyapi.api.key}")
    private String apiKey;

    private static final String PIVOT_CURRENCY = "USD";

    public double getExchangeRate(String from, String to) {
        if (from == null || to == null || from.equalsIgnoreCase(to)) return 1.0;

        String fromBase = from.toUpperCase();
        String toTarget = to.toUpperCase();

        /*
        if (shouldRefreshCache(fromBase, toTarget)) {
            refreshAllRatesFromAPI();
        }
         */

        double rateFrom = getRateFromDb(fromBase);
        double rateTo = getRateFromDb(toTarget);

        return rateTo / rateFrom;
    }

    private boolean shouldRefreshCache(String from, String to) {
        return isRateStale(from) || isRateStale(to);
    }

    private boolean isRateStale(String code) {
        if (code.equals(PIVOT_CURRENCY))
            return false;

        return !rateRepository.existsByToCurrency(code);
    }

    private double getRateFromDb(String code) {
        if (code.equals(PIVOT_CURRENCY)) return 1.0;
        return rateRepository.findByToCurrency(code)
                .map(ExchangeRate::getRate)
                .orElse(1.0);
    }

    private void refreshAllRatesFromAPI() {
        try {
            String url = String.format("https://api.currencyapi.com/v3/latest?apikey=%s&base_currency=%s", apiKey, PIVOT_CURRENCY);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");

                data.forEach((code, details) -> {
                    Map<String, Object> valMap = (Map<String, Object>) details;
                    double value = ((Number) valMap.get("value")).doubleValue();

                    ExchangeRate rate = rateRepository.findByToCurrency(code)
                            .orElse(new ExchangeRate());

                    rate.setFromCurrency(PIVOT_CURRENCY);
                    rate.setToCurrency(code);
                    rate.setRate(value);
                    rate.setLastUpdated(LocalDateTime.now());

                    rateRepository.save(rate);
                });
            }
        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to refresh rates from API: " + e.getMessage());
        }
    }
}