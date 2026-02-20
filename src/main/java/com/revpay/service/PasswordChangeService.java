package com.revpay.service;

import java.util.Scanner;

import com.revpay.dao.UserDao;
import com.revpay.dao.UserDaoImpl;
import com.revpay.security.PasswordUtil;
import com.revpay.util.Session;

public class PasswordChangeService {

    private Scanner sc = new Scanner(System.in);
    private UserDao userDao = new UserDaoImpl();

    public void changePassword() {

        int userId = Session.currentUserId;

        if (userId <= 0) {
            System.out.println("❌ Please login first.");
            return;
        }

        // 1️⃣ Current password
        System.out.print("Enter current password: ");
        String currentPassword = sc.nextLine();

        // Fetch stored password hash
        String storedPasswordHash = null;
        try {
            storedPasswordHash = userDao
                    .loginUserWithReturnId() == userId ? null : null;
        } catch (Exception e) {
            // ignored
        }

        // Instead of re-login, directly verify using DB
        String passwordHash = null;
        try {
            passwordHash = new UserDaoImpl()
                    .getSecurityCodeHash(-1); // dummy init
        } catch (Exception e) {}

        // Better approach: verify by re-querying
        // We'll directly check via login logic:
        if (!PasswordUtil.verifyPassword(
                currentPassword,
                getPasswordHashByUserId(userId))) {

            System.out.println("❌ Incorrect current password.");
            return;
        }

        // 2️⃣ PIN verification (SECURITY_CODE_HASH)
        System.out.print("Enter PIN: ");
        String pin = sc.nextLine();

        String storedPinHash = userDao.getSecurityCodeHash(userId);

        if (storedPinHash == null ||
                !PasswordUtil.verifyPassword(pin, storedPinHash)) {

            System.out.println("❌ Invalid PIN.");
            return;
        }

        // 3️⃣ New password
        System.out.print("Enter new password: ");
        String newPassword = sc.nextLine();

        String newHashedPassword = PasswordUtil.hashPassword(newPassword);
        userDao.updatePassword(userId, newHashedPassword);

        System.out.println("✅ Password changed successfully.");
    }

    // Helper method
    private String getPasswordHashByUserId(int userId) {

        String sql = "SELECT PASSWORD_HASH FROM USERS WHERE USER_ID = ?";

        try (java.sql.Connection con = com.revpay.util.DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            java.sql.ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("PASSWORD_HASH");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
