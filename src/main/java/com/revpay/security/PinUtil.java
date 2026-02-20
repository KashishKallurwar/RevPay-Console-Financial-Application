package com.revpay.security;

public class PinUtil {

    // Convert plain PIN to hashed PIN
    public static String hashPin(String pin) {
        return PasswordUtil.hashPassword(pin);
    }

    // Verify entered PIN with stored hashed PIN
    public static boolean verifyPin(String pin, String hashedPin) {
        return PasswordUtil.verifyPassword(pin, hashedPin);
    }
}
