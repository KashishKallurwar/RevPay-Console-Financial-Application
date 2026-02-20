package com.revpay.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.revpay.security.PasswordUtil;

public class PasswordUtilTest {

    @Test
    public void testPasswordHashingAndVerification() {

      
        String plainPassword = "test@123";

        
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        
        assertTrue(
            PasswordUtil.verifyPassword(plainPassword, hashedPassword),
            "Password verification should return true"
        );
    }
}

