package com.dan.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String sourceIban;
    private String targetIban;
    private double amount;
    private String currency;
    private String description;
    private LocalDateTime timestamp;
}
