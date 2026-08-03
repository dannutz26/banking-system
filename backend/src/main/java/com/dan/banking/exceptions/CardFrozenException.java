package com.dan.banking.exceptions;

public class CardFrozenException extends RuntimeException {
    public CardFrozenException(String message) {
        super(message);
    }
}
