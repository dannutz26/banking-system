package com.dan.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CardResponse {
    private Long id;
    private String cardNumber;
    private String maskedCardNumber;
    private String cardHolderName;
    private LocalDate expiryDate;
    private String cardType;
    private boolean blocked;
    private boolean disposable;
    private Long accountId;
}
