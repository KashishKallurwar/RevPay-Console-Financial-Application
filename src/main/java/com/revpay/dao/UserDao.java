package com.revpay.dao;

public interface UserDao {

    // Registration & Login
    void registerUser();
    int loginUserWithReturnId();

    // User lookup
    int getUserIdByEmailOrPhone(String input);
    int getUserIdByEmail(String email);
    String getEmailByUserId(int userId);

    // Security
    String getSecurityCodeHash(int userId);
    void updatePassword(int userId, String hashedPassword);
    boolean verifyTransactionPin(int userId, String pin);
    int getUserIdByPhone(String phone);

}
