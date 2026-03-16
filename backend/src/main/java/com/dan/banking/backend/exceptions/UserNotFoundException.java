package com.dan.banking.backend.exceptions;

public class UserNotFoundException extends BankingException {
    public UserNotFoundException(String email) {
        super("User with email " + email + " does not exist.", "USER_NOT_FOUND");
    }
}