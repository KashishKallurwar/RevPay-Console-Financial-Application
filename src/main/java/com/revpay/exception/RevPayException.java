package com.revpay.exception;

/**
 * Base custom exception for RevPay application
 */
public class RevPayException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    public RevPayException(String message) {
        super(message);
    }
}
