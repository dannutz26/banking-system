package com.dan.banking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.Currency;

@Data
@RequiredArgsConstructor
public class AccountRequest {
    private String iban = "";

    @NotNull(message = "Currency is required")
    private Currency currency;

    private String ownerEmail;
}
