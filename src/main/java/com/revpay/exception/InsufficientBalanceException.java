package com.revpay.exception;

/**
 * Thrown when wallet balance is insufficient
 */
public class InsufficientBalanceException extends RevPayException {

    private static final long serialVersionUID = 1L;

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
