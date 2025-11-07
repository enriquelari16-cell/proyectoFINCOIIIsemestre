package finanzas;

import finanzas.dao.ConexionDB;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

/**
 * Utilidad para configurar automáticamente la base de datos
 * Lee el archivo setup_database.sql y ejecuta los comandos
 */
public class DatabaseSetup {

    public static void main(String[] args) {
        System.out.println("=== CONFIGURACIÓN AUTOMÁTICA DE BASE DE DATOS ===");

        try {
            // Verificar conexión a MySQL
            System.out.println("Verificando conexión a MySQL...");
            Connection conn = ConexionDB.getConnection();
            System.out.println("✅ Conexión a MySQL exitosa");

            // Leer archivo SQL
            System.out.println("Leyendo script de configuración...");
            String sqlScript = new String(Files.readAllBytes(Paths.get("setup_database.sql")));
            System.out.println("✅ Script SQL cargado");

            // Ejecutar script
            System.out.println("Ejecutando configuración de base de datos...");
            executeSqlScript(conn, sqlScript);
            System.out.println("✅ Base de datos configurada exitosamente");

            // Cerrar conexión
            conn.close();
            System.out.println("✅ Conexión cerrada");

            System.out.println("\n🎉 ¡CONFIGURACIÓN COMPLETADA!");
            System.out.println("📝 Usuario de prueba: usuario_prueba / 123456");
            System.out.println("🚀 Puedes ejecutar la aplicación ahora");

        } catch (SQLException e) {
            System.out.println("❌ ERROR DE CONEXIÓN SQL:");
            System.out.println("Mensaje: " + e.getMessage());
            System.out.println("Código: " + e.getErrorCode());
            System.out.println("Estado: " + e.getSQLState());

            showConnectionHelp();

        } catch (IOException e) {
            System.out.println("❌ ERROR LEYENDO ARCHIVO SQL:");
            System.out.println("Asegúrate de que setup_database.sql esté en el directorio raíz");
            System.out.println("Mensaje: " + e.getMessage());

        } catch (Exception e) {
            System.out.println("❌ ERROR GENERAL:");
            System.out.println("Tipo: " + e.getClass().getName());
            System.out.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== FIN DE CONFIGURACIÓN ===");
    }

    private static void executeSqlScript(Connection conn, String sqlScript) throws SQLException {
        // Dividir el script en comandos individuales
        String[] commands = sqlScript.split(";");

        try (Statement stmt = conn.createStatement()) {
            for (String command : commands) {
                command = command.trim();
                if (!command.isEmpty() && !command.startsWith("--")) {
                    try {
                        stmt.execute(command);
                    } catch (SQLException e) {
                        // Ignorar errores de comandos que ya existen (como CREATE DATABASE IF NOT EXISTS)
                        if (!e.getMessage().contains("already exists") &&
                            !e.getMessage().contains("Duplicate entry")) {
                            throw e;
                        }
                    }
                }
            }
        }
    }

    private static void showConnectionHelp() {
        System.out.println("\n💡 SOLUCIONES PARA PROBLEMAS DE CONEXIÓN:");
        System.out.println("1. VERIFICAR MYSQL SERVER:");
        System.out.println("   - Asegúrate de que MySQL esté ejecutándose");
        System.out.println("   - En Windows: services.msc > MySQL > Iniciar");
        System.out.println("   - O usa: net start mysql");
        System.out.println();
        System.out.println("2. VERIFICAR CREDENCIALES:");
        System.out.println("   - Usuario: root (por defecto)");
        System.out.println("   - Contraseña: '' (vacía por defecto)");
        System.out.println("   - Si tienes contraseña, modifícala en ConexionDB.java");
        System.out.println();
        System.out.println("3. CREAR USUARIO MYSQL (si es necesario):");
        System.out.println("   mysql -u root -p");
        System.out.println("   CREATE USER 'root'@'localhost' IDENTIFIED BY '';");
        System.out.println("   GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost';");
        System.out.println("   FLUSH PRIVILEGES;");
        System.out.println();
        System.out.println("4. VERIFICAR PUERTO:");
        System.out.println("   - Puerto por defecto: 3306");
        System.out.println("   - Verificar con: netstat -an | findstr 3306");
        System.out.println();
        System.out.println("5. CONFIGURACIÓN AVANZADA:");
        System.out.println("   - Si usas XAMPP/WAMP, asegúrate de que esté ejecutándose");
        System.out.println("   - Si usas MySQL Workbench, verifica la conexión");
    }
}