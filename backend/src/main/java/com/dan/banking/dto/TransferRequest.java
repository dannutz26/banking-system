package com.dan.banking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    @NotBlank(message = "Source IBAN is required")
    private String fromIban;

    @NotBlank(message = "Target IBAN is required")
    private String toIban;

    @Positive(message = "Amount must be greater than zero")
    private double amount;

    private String ownerEmail;
}
