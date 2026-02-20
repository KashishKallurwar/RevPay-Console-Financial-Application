package com.revpay.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.revpay.util.DBConnection;
import com.revpay.util.EncryptionUtil;

public class PaymentMethodDaoImpl implements PaymentMethodDao {

    // ================== ADD CARD ==================
    @Override
    public void addCard(int userId, String holderName, String cardNumber,
                        String expiryDate, String cardType) {

        String encryptedCard = EncryptionUtil.encrypt(cardNumber);
        String last4 = cardNumber.substring(cardNumber.length() - 4);

        boolean isDefault = !hasAnyCard(userId);

        String sql = "INSERT INTO PAYMENT_METHODS " +
                "(METHOD_ID, USER_ID, METHOD_CATEGORY, CARD_TYPE, CARD_NUMBER_ENC, " +
                "EXPIRY_DATE, LAST4, STATUS, IS_DEFAULT, CARD_HOLDER_NAME, CREATED_AT) " +
                "VALUES (PAYMENT_METHOD_SEQ.NEXTVAL, ?, 'CARD', ?, ?, ?, ?, 'ACTIVE', ?, ?, SYSTIMESTAMP)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, cardType);
            ps.setString(3, encryptedCard);
            ps.setString(4, expiryDate);
            ps.setString(5, last4);
            ps.setString(6, isDefault ? "Y" : "N");
            ps.setString(7, holderName);

            ps.executeUpdate();
            System.out.println("✅ Card added successfully (Encrypted)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== VIEW CARDS ==================
    @Override
    public List<String> viewCards(int userId) {

        List<String> cards = new ArrayList<>();

        String sql = "SELECT METHOD_ID, CARD_TYPE, LAST4, STATUS, IS_DEFAULT " +
                "FROM PAYMENT_METHODS WHERE USER_ID=? AND METHOD_CATEGORY='CARD'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cards.add(
                        "ID: " + rs.getInt("METHOD_ID") +
                                " | Type: " + rs.getString("CARD_TYPE") +
                                " | Last4: " + rs.getString("LAST4") +
                                " | Status: " + rs.getString("STATUS") +
                                " | Default: " + rs.getString("IS_DEFAULT")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cards;
    }

    // ================== ADD BANK ==================
    @Override
    public void addBankAccount(int userId, String bankName, String accountNumber) {

        String encryptedAcc = EncryptionUtil.encrypt(accountNumber);
        boolean isDefault = !hasAnyBank(userId);

        String sql = "INSERT INTO PAYMENT_METHODS " +
                "(METHOD_ID, USER_ID, METHOD_CATEGORY, BANK_NAME, ACCOUNT_NUMBER_ENC, " +
                "STATUS, IS_DEFAULT, CREATED_AT) " +
                "VALUES (PAYMENT_METHOD_SEQ.NEXTVAL, ?, 'BANK', ?, ?, 'ACTIVE', ?, SYSTIMESTAMP)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, bankName);
            ps.setString(3, encryptedAcc);
            ps.setString(4, isDefault ? "Y" : "N");

            ps.executeUpdate();
            System.out.println("✅ Bank account added successfully (Encrypted)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== VIEW BANKS ==================
    @Override
    public List<String> viewBankAccounts(int userId) {

        List<String> banks = new ArrayList<>();

        String sql = "SELECT METHOD_ID, BANK_NAME, ACCOUNT_NUMBER_ENC, STATUS, IS_DEFAULT " +
                     "FROM PAYMENT_METHODS WHERE USER_ID=? AND METHOD_CATEGORY='BANK'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String encryptedValue = rs.getString("ACCOUNT_NUMBER_ENC");
                String decrypted;

                try {
                    decrypted = EncryptionUtil.decrypt(encryptedValue);
                } catch (Exception e) {
                    decrypted = encryptedValue; // fallback if old plain data
                }

                String last4 = decrypted.substring(decrypted.length() - 4);

                banks.add(
                        "ID: " + rs.getInt("METHOD_ID") +
                                " | Bank: " + rs.getString("BANK_NAME") +
                                " | Last4: " + last4 +
                                " | Status: " + rs.getString("STATUS") +
                                " | Default: " + rs.getString("IS_DEFAULT")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return banks;
    }

    // ================== SET DEFAULT CARD ==================
    @Override
    public void setDefaultCard(int userId, int methodId) {

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            PreparedStatement reset = con.prepareStatement(
                    "UPDATE PAYMENT_METHODS SET IS_DEFAULT='N' WHERE USER_ID=? AND METHOD_CATEGORY='CARD'");
            reset.setInt(1, userId);
            reset.executeUpdate();

            PreparedStatement set = con.prepareStatement(
                    "UPDATE PAYMENT_METHODS SET IS_DEFAULT='Y' WHERE METHOD_ID=? AND USER_ID=?");
            set.setInt(1, methodId);
            set.setInt(2, userId);
            set.executeUpdate();

            con.commit();
            System.out.println("✅ Default card updated");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== SET DEFAULT BANK ==================
    @Override
    public void setDefaultBank(int userId, int methodId) {

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            PreparedStatement reset = con.prepareStatement(
                    "UPDATE PAYMENT_METHODS SET IS_DEFAULT='N' WHERE USER_ID=? AND METHOD_CATEGORY='BANK'");
            reset.setInt(1, userId);
            reset.executeUpdate();

            PreparedStatement set = con.prepareStatement(
                    "UPDATE PAYMENT_METHODS SET IS_DEFAULT='Y' WHERE METHOD_ID=? AND USER_ID=?");
            set.setInt(1, methodId);
            set.setInt(2, userId);
            set.executeUpdate();

            con.commit();
            System.out.println("✅ Default bank updated");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== DELETE BANK ==================
    @Override
    public void deleteBankAccount(int userId, int methodId) {

        String sql = "DELETE FROM PAYMENT_METHODS WHERE METHOD_ID=? AND USER_ID=? AND METHOD_CATEGORY='BANK'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, methodId);
            ps.setInt(2, userId);
            ps.executeUpdate();

            System.out.println("✅ Bank account deleted");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== VALIDATION ==================
    @Override
    public boolean isCardActive(int userId, int methodId) {

        String sql = "SELECT COUNT(*) FROM PAYMENT_METHODS " +
                "WHERE USER_ID=? AND METHOD_ID=? AND METHOD_CATEGORY='CARD' AND STATUS='ACTIVE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, methodId);

            ResultSet rs = ps.executeQuery();
            rs.next();

            return rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean isBankActive(int userId, int methodId) {

        String sql = "SELECT COUNT(*) FROM PAYMENT_METHODS " +
                "WHERE USER_ID=? AND METHOD_ID=? AND METHOD_CATEGORY='BANK' AND STATUS='ACTIVE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, methodId);

            ResultSet rs = ps.executeQuery();
            rs.next();

            return rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ================== HELPERS ==================
    private boolean hasAnyCard(int userId) {

        String sql = "SELECT COUNT(*) FROM PAYMENT_METHODS WHERE USER_ID=? AND METHOD_CATEGORY='CARD'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            rs.next();

            return rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean hasAnyBank(int userId) {

        String sql = "SELECT COUNT(*) FROM PAYMENT_METHODS WHERE USER_ID=? AND METHOD_CATEGORY='BANK'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            rs.next();

            return rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void deleteCard(int userId, int methodId) {

        String sql = "DELETE FROM PAYMENT_METHODS " +
                     "WHERE METHOD_ID=? AND USER_ID=? AND METHOD_CATEGORY='CARD'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, methodId);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Card deleted successfully");
            } else {
                System.out.println("❌ Card not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCardStatus(int userId, int methodId, String status) {

        String sql = "UPDATE PAYMENT_METHODS SET STATUS=? " +
                     "WHERE METHOD_ID=? AND USER_ID=? AND METHOD_CATEGORY='CARD'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, methodId);
            ps.setInt(3, userId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Card status updated to " + status);
            } else {
                System.out.println("❌ Card not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasActiveDefaultCard(int userId) {

        String sql = "SELECT COUNT(*) FROM PAYMENT_METHODS " +
                     "WHERE USER_ID=? AND METHOD_CATEGORY='CARD' " +
                     "AND IS_DEFAULT='Y' AND STATUS='ACTIVE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            rs.next();

            return rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean hasActiveDefaultBank(int userId) {

        String sql = "SELECT COUNT(*) FROM PAYMENT_METHODS " +
                     "WHERE USER_ID=? AND METHOD_CATEGORY='BANK' " +
                     "AND IS_DEFAULT='Y' AND STATUS='ACTIVE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            rs.next();

            return rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}