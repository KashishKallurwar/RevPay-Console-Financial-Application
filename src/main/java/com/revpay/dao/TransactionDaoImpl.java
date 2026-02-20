package com.revpay.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.revpay.util.DBConnection;

public class TransactionDaoImpl implements TransactionDao {

    // ================= ADD SELF TRANSACTION =================
	@Override
	public void addTransaction(int userId, double amount, String type) {

	    String sql = "INSERT INTO TRANSACTIONS " +
	                 "(SENDER_ID, RECEIVER_ID, AMOUNT, TRANSACTION_TYPE, STATUS, NOTE) " +
	                 "VALUES (?, ?, ?, ?, ?, ?)";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, userId);
	        ps.setInt(2, userId); // self transaction
	        ps.setDouble(3, amount);
	        ps.setString(4, type); // ADD_MONEY or WITHDRAW
	        ps.setString(5, "SUCCESS");
	        ps.setString(6, type);

	        ps.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}



    // ================= TRANSFER =================
	@Override
	public void transferTransaction(int senderId, int receiverId, double amount, String note) {

	    String sql = "INSERT INTO TRANSACTIONS " +
	                 "(SENDER_ID, RECEIVER_ID, AMOUNT, TRANSACTION_TYPE, STATUS, NOTE) " +
	                 "VALUES (?, ?, ?, ?, ?, ?)";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, senderId);
	        ps.setInt(2, receiverId);
	        ps.setDouble(3, amount);
	        ps.setString(4, "SEND_MONEY");
	        ps.setString(5, "SUCCESS");
	        ps.setString(6, note);

	        ps.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


    // ================= VIEW ALL =================
    @Override
    public void viewTransactions(int userId) {

        String sql = "SELECT * FROM TRANSACTIONS " +
                     "WHERE SENDER_ID=? OR RECEIVER_ID=? " +
                     "ORDER BY CREATED_AT DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                    rs.getString("TRANSACTION_TYPE") +
                    " | ₹" + rs.getDouble("AMOUNT") +
                    " | " + rs.getString("STATUS") +
                    " | " + rs.getTimestamp("CREATED_AT")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= FILTER BY TYPE =================
    @Override
    public void viewTransactionsByType(int userId, String type) {

        String sql = "SELECT * FROM TRANSACTIONS " +
                     "WHERE (SENDER_ID=? OR RECEIVER_ID=?) " +
                     "AND TRANSACTION_TYPE=? " +
                     "ORDER BY CREATED_AT DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setString(3, type);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                    rs.getString("TRANSACTION_TYPE") +
                    " | ₹" + rs.getDouble("AMOUNT") +
                    " | " + rs.getTimestamp("CREATED_AT")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= SENT =================
    @Override
    public void viewSentTransactions(int userId) {

        String sql = "SELECT * FROM TRANSACTIONS " +
                     "WHERE SENDER_ID=? AND RECEIVER_ID!=? " +
                     "ORDER BY CREATED_AT DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                    "Sent ₹" + rs.getDouble("AMOUNT") +
                    " | " + rs.getTimestamp("CREATED_AT")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= RECEIVED =================
    @Override
    public void viewReceivedTransactions(int userId) {

        String sql = "SELECT * FROM TRANSACTIONS " +
                     "WHERE RECEIVER_ID=? AND SENDER_ID!=? " +
                     "ORDER BY CREATED_AT DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                    "Received ₹" + rs.getDouble("AMOUNT") +
                    " | " + rs.getTimestamp("CREATED_AT")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void viewSummary(int userId) {

        String sql = "SELECT " +
                "SUM(CASE WHEN TRANSACTION_TYPE='ADD_MONEY' THEN AMOUNT ELSE 0 END) AS TOTAL_ADDED, " +
                "SUM(CASE WHEN TRANSACTION_TYPE='WITHDRAW' THEN AMOUNT ELSE 0 END) AS TOTAL_WITHDRAW, " +
                "SUM(CASE WHEN TRANSACTION_TYPE='SEND_MONEY' THEN AMOUNT ELSE 0 END) AS TOTAL_SENT, " +
                "SUM(CASE WHEN TRANSACTION_TYPE='SEND_MONEY' AND RECEIVER_ID=? THEN AMOUNT ELSE 0 END) AS TOTAL_RECEIVED " +
                "FROM TRANSACTIONS WHERE SENDER_ID=? OR RECEIVER_ID=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                double added = rs.getDouble("TOTAL_ADDED");
                double withdraw = rs.getDouble("TOTAL_WITHDRAW");
                double sent = rs.getDouble("TOTAL_SENT");
                double received = rs.getDouble("TOTAL_RECEIVED");

                System.out.println("\n--- Transaction Summary ---");
                System.out.println("Total Added: ₹" + added);
                System.out.println("Total Withdrawn: ₹" + withdraw);
                System.out.println("Total Sent: ₹" + sent);
                System.out.println("Total Received: ₹" + received);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void viewByDateRange(int userId, String startDate, String endDate) {

        String sql = "SELECT * FROM TRANSACTIONS " +
                "WHERE (SENDER_ID=? OR RECEIVER_ID=?) " +
                "AND CREATED_AT BETWEEN TO_DATE(?, 'YYYY-MM-DD') " +
                "AND TO_DATE(?, 'YYYY-MM-DD') + 1 " +
                "ORDER BY CREATED_AT DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setString(3, startDate);
            ps.setString(4, endDate);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                        rs.getString("TRANSACTION_TYPE") +
                        " | ₹" + rs.getDouble("AMOUNT") +
                        " | " + rs.getString("STATUS") +
                        " | " + rs.getTimestamp("CREATED_AT")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void viewByAmountRange(int userId, double min, double max) {

        String sql = "SELECT * FROM TRANSACTIONS " +
                "WHERE (SENDER_ID=? OR RECEIVER_ID=?) " +
                "AND AMOUNT BETWEEN ? AND ? " +
                "ORDER BY CREATED_AT DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setDouble(3, min);
            ps.setDouble(4, max);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                        rs.getString("TRANSACTION_TYPE") +
                        " | ₹" + rs.getDouble("AMOUNT") +
                        " | " + rs.getTimestamp("CREATED_AT")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void viewByStatus(int userId, String status) {

        String sql = "SELECT * FROM TRANSACTIONS " +
                "WHERE (SENDER_ID=? OR RECEIVER_ID=?) " +
                "AND STATUS=? " +
                "ORDER BY CREATED_AT DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setString(3, status);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                        rs.getString("TRANSACTION_TYPE") +
                        " | ₹" + rs.getDouble("AMOUNT") +
                        " | " + rs.getString("STATUS") +
                        " | " + rs.getTimestamp("CREATED_AT")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void searchByKeyword(int userId, String keyword) {

        String sql = "SELECT * FROM TRANSACTIONS " +
                "WHERE (SENDER_ID=? OR RECEIVER_ID=?) " +
                "AND LOWER(NOTE) LIKE ? " +
                "ORDER BY CREATED_AT DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setString(3, "%" + keyword.toLowerCase() + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                        rs.getString("TRANSACTION_TYPE") +
                        " | ₹" + rs.getDouble("AMOUNT") +
                        " | " + rs.getTimestamp("CREATED_AT")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void exportToCSV(int userId) {

        String sql = "SELECT * FROM TRANSACTIONS " +
                "WHERE SENDER_ID=? OR RECEIVER_ID=? " +
                "ORDER BY CREATED_AT DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            java.io.FileWriter writer = new java.io.FileWriter("transactions.csv");
            writer.write("Type,Amount,Status,Date\n");

            while (rs.next()) {
                writer.write(
                        rs.getString("TRANSACTION_TYPE") + "," +
                        rs.getDouble("AMOUNT") + "," +
                        rs.getString("STATUS") + "," +
                        rs.getTimestamp("CREATED_AT") + "\n"
                );
            }

            writer.close();
            System.out.println("✅ Transactions exported to transactions.csv");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }






}
