package com.dan.banking.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class CurrencyService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${currencyapi.api.key}")
    private String apiKey;

    public double getExchangeRate(String from, String to) {
        if (from == null || to == null) return 1.0;
        if (from.equalsIgnoreCase(to)) return 1.0;

        try {
            // 2. Build the URL with explicit Uppercase
            String url = String.format(
                    "https://api.currencyapi.com/v3/latest?apikey=%s&base_currency=%s&currencies=%s",
                    apiKey, from.toUpperCase(), to.toUpperCase()
            );

            // 3. Log the URL to the console so we can see it's actually calling CurrencyAPI
            System.out.println("DEBUG: Calling CurrencyAPI -> " + url);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                Map<String, Object> targetData = (Map<String, Object>) data.get(to.toUpperCase());

                if (targetData != null && targetData.containsKey("value")) {
                    return ((Number) targetData.get("value")).doubleValue();
                }
            }
            return 1.0;
        } catch (Exception e) {
            System.err.println("CurrencyAPI Error: " + e.getMessage());
            return 1.0;
        }
    }
}