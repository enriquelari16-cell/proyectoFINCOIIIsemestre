package com.finanzas;

import java.sql.*;

/**
 * Test ultra simple para verificar MySQL
 */
public class SimpleDBTest {
    public static void main(String[] args) {
        System.out.println("=== TEST ULTRA SIMPLE MYSQL ===");

        String url = "jdbc:mysql://localhost:3306/mysql";
        String user = "root";
        String pass = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver OK");

            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Conexion OK");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT VERSION() as version");

            if (rs.next()) {
                System.out.println("MySQL Version: " + rs.getString("version"));
            }

            rs.close();
            stmt.close();
            conn.close();

            System.out.println("TEST PASSED - MySQL funciona!");

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}