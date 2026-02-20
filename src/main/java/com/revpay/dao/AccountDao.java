package com.revpay.dao;

public interface AccountDao {

    double getBalance(int userId);

    void addBalance(int userId, double amount);

    void deductBalance(int userId, double amount);
}
