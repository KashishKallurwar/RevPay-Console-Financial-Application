package com.revpay.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.revpay.util.DBConnection;

public class AccountDaoImpl implements AccountDao {

    @Override
    public double getBalance(int userId) {

        String sql = "SELECT BALANCE FROM WALLET WHERE USER_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("BALANCE");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public void addBalance(int userId, double amount) {

        String sql =
            "UPDATE WALLET SET BALANCE = BALANCE + ? WHERE USER_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setInt(2, userId);
            ps.executeUpdate();
            
            double updatedBalance = getBalance(userId);

            if (updatedBalance < 1000) {
                System.out.println("⚠ Warning: Low wallet balance!");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deductBalance(int userId, double amount) {

        String sql =
            "UPDATE WALLET SET BALANCE = BALANCE - ? WHERE USER_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
