package com.revpay.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revpay.security.PasswordUtil;
import com.revpay.util.DBConnection;
import com.revpay.util.Session;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);
    private Scanner sc = new Scanner(System.in);

    // ================= REGISTER USER =================
    @Override
    public void registerUser() {

        System.out.print("Full Name: ");
        String fullName = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Phone: ");
        String phone = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        String hashedPassword = PasswordUtil.hashPassword(password);
        
        System.out.print("Set Transaction PIN (4 digits): ");
        String pin = sc.nextLine();

        String hashedPin = PasswordUtil.hashPassword(pin);


        System.out.println("Choose Account Type:");
        System.out.println("1. Personal");
        System.out.println("2. Business");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt();
        sc.nextLine();

        String accountType = (choice == 2) ? "BUSINESS" : "PERSONAL";

        String insertUserSql =
        	    "INSERT INTO USERS (USER_ID, FULL_NAME, EMAIL, PHONE, PASSWORD_HASH, TRANSACTION_PIN, ACCOUNT_TYPE, IS_LOCKED) " +
        	    "VALUES (USER_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";


        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertUserSql)) {

        	ps.setString(1, fullName);
        	ps.setString(2, email);
        	ps.setString(3, phone);
        	ps.setString(4, hashedPassword);
        	ps.setString(5, hashedPin);   // NEW PIN
        	ps.setString(6, accountType);
        	ps.setString(7, "N");

            ps.executeUpdate();

            // ===== GET USER ID =====
            int userId = -1;
            try (PreparedStatement idPs =
                         con.prepareStatement("SELECT USER_SEQ.CURRVAL FROM dual");
                 ResultSet rs = idPs.executeQuery()) {

                if (rs.next()) {
                    userId = rs.getInt(1);
                }
            }

            if (accountType.equals("BUSINESS")) {

                BusinessDao businessDao = new BusinessDaoImpl();

                System.out.print("Business Name: ");
                String businessName = sc.nextLine();

                System.out.print("Business Type: ");
                String businessType = sc.nextLine();

                System.out.print("Tax ID: ");
                String taxId = sc.nextLine();

                System.out.print("Business Address: ");
                String address = sc.nextLine();

                System.out.print("Verification Document (File name): ");
                String verificationDoc = sc.nextLine();

                businessDao.saveBusinessDetails(
                        userId,
                        businessName,
                        businessType,
                        taxId,
                        address,
                        verificationDoc
                );
            }

            // ===== SECURITY QUESTION =====
            System.out.println("Set Security Question:");
            System.out.print("Enter Question: ");
            String question = sc.nextLine();

            System.out.print("Enter Answer: ");
            String answer = sc.nextLine();

            String secQSql =
                    "INSERT INTO USER_SECURITY_QUESTIONS (USER_ID, QUESTION, ANSWER_HASH) VALUES (?, ?, ?)";

            try (PreparedStatement sqPs = con.prepareStatement(secQSql)) {
                sqPs.setInt(1, userId);
                sqPs.setString(2, question);
                sqPs.setString(3, PasswordUtil.hashPassword(answer));
                sqPs.executeUpdate();
            }

            // ===== SECURITY CODE =====
            System.out.print("Set Security Code (numeric): ");
            String secCode = sc.nextLine();

            String secCodeSql =
                    "UPDATE USERS SET SECURITY_CODE_HASH = ? WHERE USER_ID = ?";

            try (PreparedStatement scPs = con.prepareStatement(secCodeSql)) {
                scPs.setString(1, PasswordUtil.hashPassword(secCode));
                scPs.setInt(2, userId);
                scPs.executeUpdate();
            }

            // ===== CREATE WALLET =====
            try (PreparedStatement walletPs =
                         con.prepareStatement(
                                 "INSERT INTO WALLET (USER_ID, BALANCE) VALUES (?, 0)")) {
                walletPs.setInt(1, userId);
                walletPs.executeUpdate();
            }

            System.out.println("User registered successfully");
            logger.info("New user registered with email: {}", email);

        } catch (SQLException e) {
            logger.error("Error during user registration", e);
            System.out.println("❌ Registration failed. Try again.");
        }
    }

    // ================= LOGIN USER =================
    @Override
    public int loginUserWithReturnId() {

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        String sql =
            "SELECT USER_ID, PASSWORD_HASH, ACCOUNT_TYPE, FAILED_ATTEMPTS, IS_LOCKED " +
            "FROM USERS WHERE EMAIL = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int userId = rs.getInt("USER_ID");
                String hashedPassword = rs.getString("PASSWORD_HASH");
                String accountType = rs.getString("ACCOUNT_TYPE");
                int failedAttempts = rs.getInt("FAILED_ATTEMPTS");
                String isLocked = rs.getString("IS_LOCKED");

                if ("Y".equals(isLocked)) {
                    System.out.println("❌ Account is locked due to multiple failed attempts.");
                    return -1;
                }

                if (PasswordUtil.verifyPassword(password, hashedPassword)) {

                    // reset failed attempts
                    resetFailedAttempts(userId);

                    Session.startSession(userId, accountType);

                    System.out.println("Login successful");
                    return userId;
                } else {
                    failedAttempts++;
                    updateFailedAttempts(userId, failedAttempts);

                    if (failedAttempts >= 3) {
                        lockAccount(userId);
                        System.out.println("❌ Account locked after 3 failed attempts.");
                    } else {
                        System.out.println("❌ Invalid email or password");
                    }
                }
            } else {
                System.out.println("❌ Invalid email or password");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
    private void updateFailedAttempts(int userId, int attempts) {
        String sql = "UPDATE USERS SET FAILED_ATTEMPTS = ? WHERE USER_ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, attempts);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetFailedAttempts(int userId) {
        String sql = "UPDATE USERS SET FAILED_ATTEMPTS = 0 WHERE USER_ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void lockAccount(int userId) {
        String sql = "UPDATE USERS SET IS_LOCKED = 'Y' WHERE USER_ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // ================= LOOKUPS =================
    @Override
    public int getUserIdByEmailOrPhone(String input) {
        String sql = "SELECT USER_ID FROM USERS WHERE EMAIL = ? OR PHONE = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, input);
            ps.setString(2, input);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("USER_ID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int getUserIdByEmail(String email) {
        String sql = "SELECT USER_ID FROM USERS WHERE EMAIL = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("USER_ID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public String getEmailByUserId(int userId) {
        String sql = "SELECT EMAIL FROM USERS WHERE USER_ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("EMAIL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    // ================= SECURITY =================
    @Override
    public String getSecurityCodeHash(int userId) {
        String sql = "SELECT SECURITY_CODE_HASH FROM USERS WHERE USER_ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("SECURITY_CODE_HASH");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updatePassword(int userId, String hashedPassword) {
        String sql = "UPDATE USERS SET PASSWORD_HASH = ? WHERE USER_ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            ps.executeUpdate();
            System.out.println("✅ Password reset successful");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean verifyTransactionPin(int userId, String pin) {

        String sql = "SELECT TRANSACTION_PIN FROM USERS WHERE USER_ID=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String storedHash = rs.getString("TRANSACTION_PIN");

                return PasswordUtil.verifyPassword(pin, storedHash);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    
    @Override
    public int getUserIdByPhone(String phone) {

        String sql = "SELECT USER_ID FROM USERS WHERE PHONE = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("USER_ID");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    
    

}
