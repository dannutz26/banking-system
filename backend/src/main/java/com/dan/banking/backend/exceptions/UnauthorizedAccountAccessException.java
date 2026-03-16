package com.dan.banking.backend.exceptions;

public class UnauthorizedAccountAccessException extends BankingException {
    public UnauthorizedAccountAccessException(String email) {
        super("User " + email + " is not authorized to use this account.", "UNAUTHORIZED_ACCESS");
    }
}