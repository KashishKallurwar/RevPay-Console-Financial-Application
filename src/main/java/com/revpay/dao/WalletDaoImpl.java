package com.revpay.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.revpay.util.DBConnection;

public class WalletDaoImpl implements WalletDao {

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
}
