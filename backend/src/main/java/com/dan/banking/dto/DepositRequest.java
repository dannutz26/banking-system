package com.dan.banking.dto;

import lombok.Data;

@Data
public class DepositRequest {
    private String iban;
    private Double amount;
}
