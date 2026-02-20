package com.revpay.dao;

public interface TransactionDao {

    // For self transactions (Add money, Withdraw, etc.)
    void addTransaction(int userId, double amount, String type);

    // For sending money between users
    void transferTransaction(int senderId, int receiverId, double amount, String note);

    // View all transactions (sent + received + self)
    void viewTransactions(int userId);

    // Filter transactions by type (TRANSFER, ADD_MONEY, WITHDRAW, etc.)
    void viewTransactionsByType(int userId, String type);

    // View only sent transactions
    void viewSentTransactions(int userId);

    // View only received transactions
    void viewReceivedTransactions(int userId);
    
    // View Total Summary
    void viewSummary(int userId);
    
    void viewByDateRange(int userId, String startDate, String endDate);

    void viewByAmountRange(int userId, double min, double max);

    void viewByStatus(int userId, String status);

    void searchByKeyword(int userId, String keyword);

    void exportToCSV(int userId);


}
