package com.dan.banking.backend.exceptions;

public class AccountNotFoundException extends BankingException {
    public AccountNotFoundException(String iban) {
        super("The account with IBAN " + iban + " was not found.", "ACCOUNT_NOT_FOUND");
    }
}