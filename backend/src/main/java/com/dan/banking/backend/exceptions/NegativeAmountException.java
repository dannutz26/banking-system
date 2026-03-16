package com.dan.banking.backend.exceptions;

public class NegativeAmountException extends BankingException {
    public NegativeAmountException() {
        super("Transfer amount must be greater than zero.", "INVALID_AMOUNT");
    }
}
