package com.revpay.dao;

import java.sql.*;
import com.revpay.util.DBConnection;

public class LoanDaoImpl implements LoanDao {

    // ================= APPLY LOAN =================
    @Override
    public void applyLoan(int userId,
                          double loanAmount,
                          String purpose,
                          double annualRevenue,
                          int creditScore,
                          String documentName) {

        String sql = "INSERT INTO LOANS " +
                "(LOAN_ID, USER_ID, LOAN_AMOUNT, PURPOSE, ANNUAL_REVENUE, CREDIT_SCORE, DOCUMENT_NAME, STATUS, REMAINING_BALANCE, CREATED_AT) " +
                "VALUES (LOAN_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, 'PENDING', ?, SYSTIMESTAMP)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDouble(2, loanAmount);
            ps.setString(3, purpose);
            ps.setDouble(4, annualRevenue);
            ps.setInt(5, creditScore);
            ps.setString(6, documentName);
            ps.setDouble(7, loanAmount);

            ps.executeUpdate();

            System.out.println("✅ Loan application submitted successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ================= VIEW LOANS =================
    @Override
    public void viewLoansByUser(int userId) {

        String sql = "SELECT * FROM LOANS WHERE USER_ID = ? ORDER BY CREATED_AT DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                        "Loan ID: " + rs.getInt("LOAN_ID") +
                        " | ₹" + rs.getDouble("LOAN_AMOUNT") +
                        " | Remaining: ₹" + rs.getDouble("REMAINING_BALANCE") +
                        " | Status: " + rs.getString("STATUS")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ================= VIEW STATUS =================
    @Override
    public void viewLoanStatus(int userId) {

        String sql = "SELECT LOAN_ID, STATUS FROM LOANS WHERE USER_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                        "Loan ID: " + rs.getInt("LOAN_ID") +
                        " | Status: " + rs.getString("STATUS")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ================= GET REMAINING =================
    @Override
    public double getRemainingBalance(int loanId) {

        String sql = "SELECT REMAINING_BALANCE FROM LOANS WHERE LOAN_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, loanId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("REMAINING_BALANCE");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    // ================= REPAY LOAN =================
    @Override
    public void repayLoan(int loanId, int userId, double amount) {

        double remaining = getRemainingBalance(loanId);

        if (remaining <= 0) {
            System.out.println("❌ Loan already fully paid");
            return;
        }

        if (amount > remaining) {
            System.out.println("❌ Amount exceeds remaining balance");
            return;
        }

        String updateLoan = "UPDATE LOANS SET REMAINING_BALANCE = REMAINING_BALANCE - ? WHERE LOAN_ID = ?";
        String insertRepay = "INSERT INTO LOAN_REPAYMENTS " +
                "(REPAYMENT_ID, LOAN_ID, USER_ID, AMOUNT, PAID_AT) " +
                "VALUES (LOAN_REPAY_SEQ.NEXTVAL, ?, ?, ?, SYSTIMESTAMP)";

        try (Connection con = DBConnection.getConnection()) {

            // Update remaining balance
            try (PreparedStatement ps1 = con.prepareStatement(updateLoan)) {
                ps1.setDouble(1, amount);
                ps1.setInt(2, loanId);
                ps1.executeUpdate();
            }

            // Insert repayment record
            try (PreparedStatement ps2 = con.prepareStatement(insertRepay)) {
                ps2.setInt(1, loanId);
                ps2.setInt(2, userId);
                ps2.setDouble(3, amount);
                ps2.executeUpdate();
            }

            System.out.println("✅ Loan repayment successful");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ================= REPAYMENT HISTORY =================
    @Override
    public void viewRepaymentHistory(int loanId, int userId) {

        String sql = "SELECT * FROM LOAN_REPAYMENTS WHERE LOAN_ID = ? AND USER_ID = ? ORDER BY PAID_AT DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, loanId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                        "Repayment ID: " + rs.getInt("REPAYMENT_ID") +
                        " | ₹" + rs.getDouble("AMOUNT") +
                        " | Paid At: " + rs.getTimestamp("PAID_AT")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

