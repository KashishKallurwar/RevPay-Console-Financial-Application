package com.revpay.dao;

import java.sql.*;
import com.revpay.util.DBConnection;

public class InvoiceDaoImpl implements InvoiceDao {

    // ================= CREATE EMPTY INVOICE =================
    @Override
    public int createEmptyInvoice(int businessId, int customerId) {

        String insertSql =
                "INSERT INTO INVOICES " +
                "(INVOICE_ID, BUSINESS_ID, CUSTOMER_ID, AMOUNT, STATUS, CREATED_AT) " +
                "VALUES (INVOICE_SEQ.NEXTVAL, ?, ?, 0, 'UNPAID', SYSTIMESTAMP)";

        String seqSql = "SELECT INVOICE_SEQ.CURRVAL FROM DUAL";

        try (Connection con = DBConnection.getConnection()) {

            // 1️⃣ Insert invoice
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setInt(1, businessId);
                ps.setInt(2, customerId);
                ps.executeUpdate();
            }

            // 2️⃣ Get generated ID
            try (PreparedStatement ps2 = con.prepareStatement(seqSql);
                 ResultSet rs = ps2.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }


    // ================= ADD INVOICE ITEM =================
    @Override
    public void addInvoiceItem(int invoiceId, String desc,
                               int qty, double unitPrice, double total) {

        String sql =
                "INSERT INTO INVOICE_ITEMS " +
                "(ITEM_ID, INVOICE_ID, DESCRIPTION, QUANTITY, UNIT_PRICE, TOTAL_PRICE) " +
                "VALUES (INVOICE_ITEM_SEQ.NEXTVAL, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            ps.setString(2, desc);
            ps.setInt(3, qty);
            ps.setDouble(4, unitPrice);
            ps.setDouble(5, total);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ================= FINALIZE INVOICE =================
    @Override
    public void finalizeInvoice(int invoiceId,
                                double totalAmount,
                                String paymentTerms) {

        String sql =
                "UPDATE INVOICES " +
                "SET AMOUNT = ?, PAYMENT_TERMS = ?, UPDATED_AT = SYSTIMESTAMP " +
                "WHERE INVOICE_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, totalAmount);
            ps.setString(2, paymentTerms);
            ps.setInt(3, invoiceId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ================= VIEW BUSINESS INVOICES =================
    @Override
    public void viewInvoicesByBusiness(int businessId) {

        String sql = "SELECT * FROM INVOICES WHERE BUSINESS_ID = ? ORDER BY CREATED_AT DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, businessId);

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println(
                        "ID: " + rs.getInt("INVOICE_ID") +
                        " | ₹" + rs.getDouble("AMOUNT") +
                        " | Status: " + rs.getString("STATUS") +
                        " | Created: " + rs.getTimestamp("CREATED_AT")
                );
            }

            if (!found) {
                System.out.println("No invoices found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // ================= VIEW UNPAID =================
    @Override
    public void viewInvoicesByStatus(int businessId) {

        String sql =
                "SELECT * FROM INVOICES " +
                "WHERE BUSINESS_ID = ? AND STATUS = 'UNPAID'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, businessId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                        "Unpaid Invoice ID: " +
                        rs.getInt("INVOICE_ID") +
                        " | ₹" + rs.getDouble("AMOUNT")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ================= VIEW CUSTOMER INVOICES =================
    @Override
    public void viewInvoicesByCustomer(int customerId) {

        String sql =
                "SELECT * FROM INVOICES WHERE CUSTOMER_ID = ? ORDER BY CREATED_AT DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                        "Invoice ID: " + rs.getInt("INVOICE_ID") +
                        " | ₹" + rs.getDouble("AMOUNT") +
                        " | Status: " + rs.getString("STATUS")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ================= CHECK UNPAID =================
    @Override
    public boolean isInvoiceUnpaid(int invoiceId) {

        String sql = "SELECT STATUS FROM INVOICES WHERE INVOICE_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return "UNPAID".equals(rs.getString("STATUS"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    // ================= GET AMOUNT =================
    @Override
    public double getInvoiceAmount(int invoiceId) {

        String sql = "SELECT AMOUNT FROM INVOICES WHERE INVOICE_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("AMOUNT");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    // ================= GET BUSINESS ID =================
    @Override
    public int getBusinessId(int invoiceId) {

        String sql = "SELECT BUSINESS_ID FROM INVOICES WHERE INVOICE_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("BUSINESS_ID");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }


    // ================= MARK PAID =================
    @Override
    public void markInvoicePaid(int invoiceId) {

        String sql =
                "UPDATE INVOICES SET STATUS = 'PAID', UPDATED_AT = SYSTIMESTAMP WHERE INVOICE_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
