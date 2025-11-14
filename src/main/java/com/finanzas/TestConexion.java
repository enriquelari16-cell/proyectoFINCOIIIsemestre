package com.finanzas;

import com.finanzas.dao.ConexionDB;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Clase de prueba para verificar la conexión a MySQL
 */
public class TestConexion {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE CONEXIÓN MYSQL ===");

        try {
            System.out.println("Intentando conectar a la base de datos...");
            Connection conn = ConexionDB.getConnection();

            if (conn != null) {
                System.out.println("✅ CONEXIÓN EXITOSA");
                System.out.println("Conexión establecida: " + conn.toString());

                // Cerrar conexión
                conn.close();
                System.out.println("Conexión cerrada correctamente");
            } else {
                System.out.println("❌ CONEXIÓN FALLIDA: Connection es null");
            }

        } catch (SQLException e) {
            System.out.println("❌ ERROR DE CONEXIÓN:");
            System.out.println("Mensaje: " + e.getMessage());
            System.out.println("Código de error SQL: " + e.getErrorCode());
            System.out.println("Estado SQL: " + e.getSQLState());

            // Diagnóstico específico
            if (e.getMessage().contains("Driver")) {
                System.out.println("\n💡 POSIBLE SOLUCIÓN:");
                System.out.println("1. Verificar que el JAR del conector esté en el classpath");
                System.out.println("2. Verificar que la clase del driver sea correcta");
                System.out.println("3. Intentar con una versión diferente del conector MySQL");
            } else if (e.getMessage().contains("Communications link failure")) {
                System.out.println("\n💡 POSIBLE SOLUCIÓN:");
                System.out.println("1. Verificar que MySQL Server esté ejecutándose");
                System.out.println("2. Verificar la URL de conexión: jdbc:mysql://localhost:3306/finanzas_personales");
                System.out.println("3. Verificar que el puerto 3306 esté abierto");
            } else if (e.getMessage().contains("Access denied")) {
                System.out.println("\n💡 POSIBLE SOLUCIÓN:");
                System.out.println("1. Verificar usuario y contraseña en ConexionDB.java");
                System.out.println("2. Verificar permisos del usuario en MySQL");
                System.out.println("3. Crear usuario con permisos adecuados");
            } else if (e.getMessage().contains("Unknown database")) {
                System.out.println("\n💡 POSIBLE SOLUCIÓN:");
                System.out.println("1. Crear la base de datos: CREATE DATABASE finanzas_personales;");
                System.out.println("2. Verificar el nombre de la base de datos en la URL");
            }

        } catch (Exception e) {
            System.out.println("❌ ERROR GENERAL:");
            System.out.println("Tipo: " + e.getClass().getName());
            System.out.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== FIN DE PRUEBA ===");
    }
}