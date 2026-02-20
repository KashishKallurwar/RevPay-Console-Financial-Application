package com.revpay.dao;

import java.sql.*;
import com.revpay.util.DBConnection;

public class BusinessAnalyticsDaoImpl implements BusinessAnalyticsDao {

    // ================= TRANSACTION SUMMARY =================
    @Override
    public void showTransactionSummary(int businessId) {

        String sql =
                "SELECT " +
                "COUNT(*) AS TOTAL_TXNS, " +
                "NVL(SUM(CASE WHEN RECEIVER_ID = ? THEN AMOUNT END),0) AS TOTAL_RECEIVED, " +
                "NVL(SUM(CASE WHEN SENDER_ID = ? THEN AMOUNT END),0) AS TOTAL_SENT " +
                "FROM TRANSACTIONS " +
                "WHERE SENDER_ID = ? OR RECEIVER_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, businessId);
            ps.setInt(2, businessId);
            ps.setInt(3, businessId);
            ps.setInt(4, businessId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n===== TRANSACTION SUMMARY =====");
                System.out.println("Total Transactions : " + rs.getInt("TOTAL_TXNS"));
                System.out.println("Total Received     : ₹" + rs.getDouble("TOTAL_RECEIVED"));
                System.out.println("Total Sent         : ₹" + rs.getDouble("TOTAL_SENT"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= REVENUE REPORT =================
    @Override
    public void showRevenueReport(int businessId) {

        String sql =
                "SELECT NVL(SUM(AMOUNT),0) AS TOTAL_REVENUE, " +
                "COUNT(*) AS TOTAL_PAID " +
                "FROM INVOICES " +
                "WHERE BUSINESS_ID = ? AND STATUS = 'PAID'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, businessId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n===== REVENUE REPORT =====");
                System.out.println("Total Revenue (Paid Invoices): ₹" + rs.getDouble("TOTAL_REVENUE"));
                System.out.println("Total Paid Invoices          : " + rs.getInt("TOTAL_PAID"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= OUTSTANDING INVOICES =================
    @Override
    public void showOutstandingInvoices(int businessId) {

        String sql =
                "SELECT COUNT(*) AS UNPAID_COUNT, " +
                "NVL(SUM(AMOUNT),0) AS TOTAL_PENDING " +
                "FROM INVOICES " +
                "WHERE BUSINESS_ID = ? AND STATUS = 'UNPAID'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, businessId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n===== OUTSTANDING INVOICES =====");
                System.out.println("Unpaid Invoice Count : " + rs.getInt("UNPAID_COUNT"));
                System.out.println("Total Pending Amount : ₹" + rs.getDouble("TOTAL_PENDING"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= PAYMENT TRENDS (MONTHLY) =================
    @Override
    public void showPaymentTrends(int businessId) {

        String sql =
                "SELECT TO_CHAR(CREATED_AT, 'YYYY-MM') AS MONTH, " +
                "NVL(SUM(AMOUNT),0) AS MONTHLY_REVENUE " +
                "FROM INVOICES " +
                "WHERE BUSINESS_ID = ? AND STATUS = 'PAID' " +
                "GROUP BY TO_CHAR(CREATED_AT, 'YYYY-MM') " +
                "ORDER BY MONTH";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, businessId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== PAYMENT TRENDS (Monthly Revenue) =====");

            while (rs.next()) {
                System.out.println(
                        "Month: " + rs.getString("MONTH") +
                        " | Revenue: ₹" + rs.getDouble("MONTHLY_REVENUE")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= TOP CUSTOMERS =================
    @Override
    public void showTopCustomers(int businessId) {

        String sql =
                "SELECT CUSTOMER_ID, NVL(SUM(AMOUNT),0) AS TOTAL_PAID " +
                "FROM INVOICES " +
                "WHERE BUSINESS_ID = ? AND STATUS = 'PAID' " +
                "GROUP BY CUSTOMER_ID " +
                "ORDER BY TOTAL_PAID DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, businessId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== TOP CUSTOMERS =====");

            while (rs.next()) {
                System.out.println(
                        "Customer ID: " + rs.getInt("CUSTOMER_ID") +
                        " | Total Paid: ₹" + rs.getDouble("TOTAL_PAID")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}