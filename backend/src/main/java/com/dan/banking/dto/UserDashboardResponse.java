package com.dan.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDashboardResponse {
    private String firstName;
    private double totalNetWorth;
    private String preferredCurrency;
    private String primaryIban;
}
