package com.dan.banking.backend.dto;

import lombok.Data;

@Data
public class DepositRequest {
    private String iban;
    private Double amount;
}