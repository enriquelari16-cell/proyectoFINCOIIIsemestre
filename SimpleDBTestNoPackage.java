import java.sql.*;

/**
 * Test ultra simple SIN paquete para verificar MySQL
 */
public class SimpleDBTestNoPackage {
    public static void main(String[] args) {
        System.out.println("=== TEST ULTRA SIMPLE MYSQL - SIN PAQUETE ===");

        String url = "jdbc:mysql://localhost:3306/mysql?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String pass = "";

        try {
            // Cargar driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ Driver cargado");

            // Conectar
            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("✅ Conexión exitosa");

            // Query simple
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT VERSION() as version");

            if (rs.next()) {
                System.out.println("✅ MySQL Version: " + rs.getString("version"));
            }

            // Cerrar
            rs.close();
            stmt.close();
            conn.close();
            System.out.println("✅ Conexión cerrada");

            System.out.println("\n🎉 ¡TEST PASSED! MySQL funciona correctamente");
            System.out.println("Ahora puedes usar la aplicación financiera");

        } catch (ClassNotFoundException e) {
            System.out.println("❌ ERROR: Driver MySQL no encontrado");
            System.out.println("Solución: Descarga mysql-connector-java-8.0.33.jar");
            System.out.println("         y colócalo en com/lib/");
        } catch (SQLException e) {
            System.out.println("❌ ERROR SQL: " + e.getMessage());
            System.out.println("Código: " + e.getErrorCode());
            System.out.println("Estado: " + e.getSQLState());

            if (e.getMessage().contains("Communications link")) {
                System.out.println("\n💡 SOLUCIÓN: MySQL Server no está ejecutándose");
                System.out.println("   - Inicia MySQL desde servicios");
                System.out.println("   - O usa: net start mysql");
            } else if (e.getMessage().contains("Access denied")) {
                System.out.println("\n💡 SOLUCIÓN: Credenciales incorrectas");
                System.out.println("   - Usuario: root");
                System.out.println("   - Contraseña: (vacía por defecto)");
            } else if (e.getMessage().contains("Unknown database")) {
                System.out.println("\n💡 SOLUCIÓN: Base de datos no existe");
                System.out.println("   - Ejecuta: java -cp \".\" DatabaseSetup");
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR GENERAL: " + e.getMessage());
        }

        System.out.println("\n=== FIN DEL TEST ===");
    }
}