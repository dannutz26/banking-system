package com.dan.banking.backend.exceptions;

public class InsuficientFundsException extends BankingException {
    public InsuficientFundsException() {
        super("Insufficient funds for this transaction.", "INSUFFICIENT_FUNDS");
    }
}
