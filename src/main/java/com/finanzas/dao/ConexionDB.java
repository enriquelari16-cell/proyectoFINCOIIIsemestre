package com.finanzas.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    // Configuración compatible con MySQL Connector/J 8.0.x
    private static final String URL = "jdbc:mysql://localhost:3306/finanzas_personales?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root"; // Cambiar por tu usuario
    private static final String CONTRASENA = null; // Cambiar por tu contraseña

    public static Connection getConnection() throws SQLException {
        try {
            // Intentar con el driver moderno primero (MySQL Connector/J 8.0+)
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ Usando MySQL Connector/J moderno");
        } catch (ClassNotFoundException e) {
            try {
                // Fallback al driver legacy (MySQL Connector/J 5.x)
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("⚠️ Usando MySQL Connector/J legacy");
            } catch (ClassNotFoundException ex) {
                throw new SQLException("Driver MySQL no encontrado. Asegúrate de que mysql-connector-java-8.0.33.jar esté en el classpath", ex);
            }
        }

        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}