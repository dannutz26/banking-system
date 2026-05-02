package com.dan.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    private String fromIban;
    private String toIban;
    private double amount;
    private String ownerEmail;
}
