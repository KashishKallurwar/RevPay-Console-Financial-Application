package com.revpay.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.revpay.model.Notification;
import com.revpay.util.DBConnection;

public class NotificationDaoImpl implements NotificationDao {

    // ================= ADD NOTIFICATION =================
    @Override
    public void addNotification(int userId, String message, String type) {

        String sql = "INSERT INTO NOTIFICATIONS " +
                     "(NOTIFICATION_ID, USER_ID, MESSAGE, TYPE, IS_READ, CREATED_AT) " +
                     "VALUES (NOTIF_SEQ.NEXTVAL, ?, ?, ?, 'N', SYSTIMESTAMP)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, message);
            ps.setString(3, type);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= GET NOTIFICATIONS LIST =================
    @Override
    public List<Notification> getNotificationsByUser(int userId) {

        List<Notification> list = new ArrayList<>();

        String sql = "SELECT * FROM NOTIFICATIONS " +
                     "WHERE USER_ID = ? ORDER BY CREATED_AT DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Notification n = new Notification();
                n.setNotificationId(rs.getInt("NOTIFICATION_ID"));
                n.setUserId(rs.getInt("USER_ID"));
                n.setMessage(rs.getString("MESSAGE"));
                n.setType(rs.getString("TYPE"));
                n.setIsRead(rs.getString("IS_READ"));
                n.setCreatedAt(rs.getTimestamp("CREATED_AT"));

                list.add(n);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================= MARK AS READ =================
    @Override
    public void markAsRead(int notificationId) {

        String sql = "UPDATE NOTIFICATIONS SET IS_READ = 'Y' WHERE NOTIFICATION_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, notificationId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= SHOW NOTIFICATIONS (Console) =================
    @Override
    public void showNotifications(int userId) {

        String sql =
            "SELECT notification_id, message, type, is_read, created_at " +
            "FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n----- NOTIFICATIONS -----");

            boolean found = false;

            while (rs.next()) {
                found = true;

                String status = rs.getString("is_read").equals("N") ? "🔔 NEW" : "✓ Read";

                System.out.println(
                    "[" + rs.getString("type") + "] " +
                    rs.getString("message") +
                    " | " +
                    rs.getTimestamp("created_at") +
                    " | " +
                    status
                );
            }

            if (!found) {
                System.out.println("No notifications");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
