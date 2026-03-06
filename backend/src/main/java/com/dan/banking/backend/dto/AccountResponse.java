package com.dan.banking.backend.dto;

public class AccountResponse {
    private String iban;
    private Double balance;
    private String currency;

    public AccountResponse(String iban, Double balance, String currency) {
        this.iban = iban;
        this.balance = balance;
        this.currency = currency;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
