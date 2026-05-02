package com.dan.banking.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.Currency;

@Data
@RequiredArgsConstructor
public class AccountRequest {
    private String iban = "";
    private Currency currency;
    private String ownerEmail;
}
