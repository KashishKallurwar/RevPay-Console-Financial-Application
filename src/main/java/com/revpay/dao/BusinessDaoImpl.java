package com.revpay.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import com.revpay.util.DBConnection;

public class BusinessDaoImpl implements BusinessDao {

    @Override
    public void registerBusiness(int userId, String name, String type, String taxId, String address) {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO business_details VALUES (?, ?, ?, ?, ?)"
            );
            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setString(3, type);
            ps.setString(4, taxId);
            ps.setString(5, address);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveBusinessDetails(
            int userId,
            String businessName,
            String businessType,
            String taxId,
            String address,
            String verificationDoc) {

        String sql = "INSERT INTO BUSINESS_DETAILS " +
                "(BUSINESS_ID, USER_ID, BUSINESS_NAME, BUSINESS_TYPE, TAX_ID, ADDRESS, VERIFICATION_DOC) " +
                "VALUES (BUSINESS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, businessName);
            ps.setString(3, businessType);
            ps.setString(4, taxId);
            ps.setString(5, address);
            ps.setString(6, verificationDoc);

            ps.executeUpdate();

            System.out.println("✅ Business details saved successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
