package com.dan.banking.backend.utility;

import org.springframework.stereotype.Component;

@Component
public class IbanGenerator {
    public String generate(String countryCode) {
        String bankCode = "BANK";

        StringBuilder accountNumber = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 16; i++) {
            accountNumber.append(random.nextInt(10));
        }

        return (countryCode + "87" + bankCode + accountNumber).toUpperCase();
    }
}
