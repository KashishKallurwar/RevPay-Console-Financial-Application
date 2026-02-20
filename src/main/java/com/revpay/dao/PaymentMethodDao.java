package com.revpay.dao;

import java.util.List;

public interface PaymentMethodDao {

    // CARD OPERATIONS
    void addCard(int userId, String holderName, String cardNumber,
                 String expiryDate, String cardType);

    List<String> viewCards(int userId);

    void setDefaultCard(int userId, int methodId);

    void deleteCard(int userId, int methodId);

    void updateCardStatus(int userId, int methodId, String status); // ACTIVE / BLOCKED


    // BANK OPERATIONS (Business Only)
    void addBankAccount(int userId, String bankName, String accountNumber);

    List<String> viewBankAccounts(int userId);

    void setDefaultBank(int userId, int methodId);

    void deleteBankAccount(int userId, int methodId);


    // VALIDATION METHODS (Used by Wallet)
    boolean hasActiveDefaultCard(int userId);

    boolean hasActiveDefaultBank(int userId);
    
    boolean isCardActive(int userId, int methodId);
    
    boolean isBankActive(int userId, int methodId);

}
