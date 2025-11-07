import java.sql.*;

/**
 * Script simple para crear la base de datos
 */
public class CreateDatabase {
    public static void main(String[] args) {
        System.out.println("=== CREANDO BASE DE DATOS FINANZAS_PERSONALES ===");

        String url = "jdbc:mysql://localhost:3306/mysql?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String pass = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);

            Statement stmt = conn.createStatement();

            // Crear base de datos
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS finanzas_personales");
            System.out.println("✅ Base de datos creada");

            // Usar la base de datos
            stmt.executeUpdate("USE finanzas_personales");

            // Crear tablas
            String[] tables = {
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "nombre VARCHAR(100) NOT NULL UNIQUE, " +
                "edad TINYINT NOT NULL CHECK (edad > 0 AND edad < 120), " +
                "tipo_uso ENUM('Personal', 'Familiar', 'Empresarial') NOT NULL DEFAULT 'Personal', " +
                "contrasena VARCHAR(255) NOT NULL, " +
                "presupuesto_inicial DECIMAL(15,2) NOT NULL DEFAULT 0.00, " +
                "presupuesto_actual DECIMAL(15,2) NOT NULL DEFAULT 0.00, " +
                "fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",

                "CREATE TABLE IF NOT EXISTS categorias (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "usuario_id INT NOT NULL, " +
                "nombre VARCHAR(100) NOT NULL, " +
                "presupuesto DECIMAL(15,2) NOT NULL DEFAULT 0.00, " +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS transacciones (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "usuario_id INT NOT NULL, " +
                "tipo ENUM('Ingreso', 'Gasto') NOT NULL, " +
                "monto DECIMAL(15,2) NOT NULL CHECK (monto > 0), " +
                "descripcion TEXT, " +
                "fecha DATE NOT NULL DEFAULT (CURRENT_DATE), " +
                "saldo_despues DECIMAL(15,2) NOT NULL, " +
                "categoria_id INT, " +
                "fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL)",

                "CREATE TABLE IF NOT EXISTS metas (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "usuario_id INT NOT NULL, " +
                "nombre VARCHAR(200) NOT NULL, " +
                "monto_objetivo DECIMAL(15,2) NOT NULL CHECK (monto_objetivo > 0), " +
                "ahorro_actual DECIMAL(15,2) NOT NULL DEFAULT 0.00, " +
                "descripcion TEXT, " +
                "fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE)"
            };

            for (String table : tables) {
                stmt.executeUpdate(table);
            }
            System.out.println("✅ Tablas creadas");

            // Insertar usuario de prueba
            stmt.executeUpdate("INSERT IGNORE INTO usuarios (nombre, edad, tipo_uso, contrasena, presupuesto_inicial, presupuesto_actual) " +
                             "VALUES ('usuario_prueba', 25, 'Personal', '123456', 10000.00, 10000.00)");
            System.out.println("✅ Usuario de prueba creado");

            stmt.close();
            conn.close();

            System.out.println("\n🎉 ¡BASE DE DATOS CONFIGURADA EXITOSAMENTE!");
            System.out.println("📝 Usuario de prueba: usuario_prueba / 123456");
            System.out.println("🚀 Ahora puedes ejecutar la aplicación");

        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}