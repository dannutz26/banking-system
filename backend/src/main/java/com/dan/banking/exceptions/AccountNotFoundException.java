package com.dan.banking.exceptions;

public class AccountNotFoundException extends BankingException {
    public AccountNotFoundException(String iban) {
        super("The account with IBAN " + iban + " was not found.", "ACCOUNT_NOT_FOUND");
    }
}
