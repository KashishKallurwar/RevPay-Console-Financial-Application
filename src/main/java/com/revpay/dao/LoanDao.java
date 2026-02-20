package com.revpay.dao;

public interface LoanDao {

    // Apply for new loan
    void applyLoan(int userId,
                   double loanAmount,
                   String purpose,
                   double annualRevenue,
                   int creditScore,
                   String documentName);

    // View all loans of a user
    void viewLoansByUser(int userId);

    // Check loan status
    void viewLoanStatus(int userId);

    // Get remaining balance
    double getRemainingBalance(int loanId);

    // Repay loan
    void repayLoan(int loanId, int userId, double amount);

    // View repayment history
    void viewRepaymentHistory(int loanId, int userId);
}
