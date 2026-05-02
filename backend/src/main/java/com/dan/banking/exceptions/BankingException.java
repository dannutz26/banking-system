package com.dan.banking.exceptions;

import lombok.Getter;

@Getter
public abstract class BankingException extends RuntimeException {
    private final String errorCode;

    public BankingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
