package finanzas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Test simple de conexión MySQL sin dependencias de paquetes
 */
public class TestConexionSimple {
    public static void main(String[] args) {
        System.out.println("=== TEST SIMPLE DE CONEXIÓN MYSQL ===");

        // Configuración de conexión
        String url = "jdbc:mysql://localhost:3306/finanzas_personales";
        String usuario = "root";
        String contrasena = "";

        try {
            // Cargar el driver
            System.out.println("Cargando driver MySQL...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ Driver cargado correctamente");

            // Intentar conexión
            System.out.println("Intentando conectar a: " + url);
            Connection conn = DriverManager.getConnection(url, usuario, contrasena);

            if (conn != null) {
                System.out.println("✅ CONEXIÓN EXITOSA");
                System.out.println("Conexión: " + conn.toString());
                conn.close();
                System.out.println("Conexión cerrada correctamente");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("❌ ERROR: Driver MySQL no encontrado");
            System.out.println("Mensaje: " + e.getMessage());
            System.out.println("\n💡 SOLUCIÓN:");
            System.out.println("1. Verificar que mysql-connector-j-9.5.0.jar esté en com/lib/");
            System.out.println("2. Verificar que esté en el classpath al compilar y ejecutar");
            System.out.println("3. Intentar con una versión anterior del conector MySQL");

        } catch (SQLException e) {
            System.out.println("❌ ERROR DE CONEXIÓN SQL:");
            System.out.println("Mensaje: " + e.getMessage());
            System.out.println("Código de error: " + e.getErrorCode());
            System.out.println("Estado SQL: " + e.getSQLState());

            if (e.getMessage().contains("Communications link failure")) {
                System.out.println("\n💡 POSIBLE SOLUCIÓN:");
                System.out.println("1. Verificar que MySQL Server esté ejecutándose");
                System.out.println("2. Verificar que el puerto 3306 esté abierto");
                System.out.println("3. Probar con 'telnet localhost 3306'");
            } else if (e.getMessage().contains("Access denied")) {
                System.out.println("\n💡 POSIBLE SOLUCIÓN:");
                System.out.println("1. Verificar usuario y contraseña");
                System.out.println("2. Crear usuario con permisos adecuados:");
                System.out.println("   CREATE USER 'root'@'localhost' IDENTIFIED BY '';");
                System.out.println("   GRANT ALL PRIVILEGES ON finanzas_personales.* TO 'root'@'localhost';");
            } else if (e.getMessage().contains("Unknown database")) {
                System.out.println("\n💡 POSIBLE SOLUCIÓN:");
                System.out.println("1. Crear la base de datos:");
                System.out.println("   CREATE DATABASE finanzas_personales;");
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR GENERAL:");
            System.out.println("Tipo: " + e.getClass().getName());
            System.out.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== FIN DEL TEST ===");
    }
}