package com.revpay.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
        "jdbc:oracle:thin:@localhost:1521/freepdb1";

    private static final String USERNAME = "REVPAY";        
    private static final String PASSWORD = "revpay123";    

    public static Connection getConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            //System.out.println("✅ Database connected successfully");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException("❌ Database connection failed", e);
        }
    }
}
