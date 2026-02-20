package com.revpay.exception;

/**
 * Thrown when user is not found in system
 */
public class UserNotFoundException extends RevPayException {
	private static final long serialVersionUID = 1L;

    public UserNotFoundException(String message) {
        super(message);
    }
}
