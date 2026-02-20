package com.revpay.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.revpay.util.DBConnection;

public class RequestMoneyDaoImpl implements RequestMoneyDao {

    // ================= CREATE REQUEST =================
    @Override
    public void createRequest(int requesterId, int receiverId, double amount) {

        String sql =
            "INSERT INTO money_requests (requester_id, receiver_id, amount, status) " +
            "VALUES (?, ?, ?, 'UNPAID')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, requesterId);
            ps.setInt(2, receiverId);
            ps.setDouble(3, amount);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= VIEW UNPAID REQUESTS =================
    @Override
    public void viewRequests(int userId) {

        String sql =
        	      "SELECT * FROM money_requests WHERE receiver_id = ? AND status = 'UNPAID'";
       
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n-----UNPAID MONEY REQUESTS -----");

            while (rs.next()) {
                System.out.println(
                    "Request ID: " + rs.getInt("request_id") +
                    " | From User: " + rs.getInt("requester_id") +
                    " | Amount: ₹" + rs.getDouble("amount") +
                    " | Status: " + rs.getString("status")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= UPDATE STATUS =================
    @Override
    public void updateRequestStatus(int requestId, String status) {

        String sql =
            "UPDATE money_requests SET status = ? WHERE request_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, requestId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= FETCH REQUEST BY ID =================
    @Override
    public RequestDetails getRequestById(int requestId) {

        String sql =
            "SELECT * FROM money_requests WHERE request_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                RequestDetails r = new RequestDetails();
                r.requestId = rs.getInt("request_id");
                r.requesterId = rs.getInt("requester_id");
                r.requestedId = rs.getInt("receiver_id");
                r.amount = rs.getDouble("amount");
                r.status = rs.getString("status");
                return r;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public void cancelRequest(int requestId, int userId) {

        String sql = "UPDATE MONEY_REQUEST " +
                     "SET STATUS='CANCELLED' " +
                     "WHERE REQUEST_ID=? " +
                     "AND SENDER_ID=? " +
                     "AND STATUS='UNPAID'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Request cancelled successfully");
            } else {
                System.out.println("❌ Cannot cancel this request");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
