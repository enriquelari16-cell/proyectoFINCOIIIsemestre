import java.sql.*;

/**
 * Test final de conexión a la base de datos finanzas_personales
 */
public class TestFinalConnection {
    public static void main(String[] args) {
        System.out.println("=== TEST FINAL DE CONEXIÓN A FINANZAS_PERSONALES ===");

        String url = "jdbc:mysql://localhost:3306/finanzas_personales?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String pass = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ Driver MySQL cargado");

            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("✅ Conexión a finanzas_personales exitosa");

            Statement stmt = conn.createStatement();

            // Verificar tablas
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            System.out.println("📋 Tablas encontradas:");
            while (rs.next()) {
                System.out.println("   - " + rs.getString(1));
            }
            rs.close();

            // Verificar usuario de prueba
            rs = stmt.executeQuery("SELECT COUNT(*) as total FROM usuarios");
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("👥 Usuarios registrados: " + total);
                if (total > 0) {
                    System.out.println("✅ Usuario de prueba encontrado");
                }
            }
            rs.close();

            stmt.close();
            conn.close();

            System.out.println("✅ Conexión cerrada correctamente");
            System.out.println("\n🎉 ¡TODO LISTO! La aplicación está configurada correctamente");
            System.out.println("🚀 Ejecuta: java -cp \".;com/lib/mysql-connector-java-8.0.33.jar\" finanzas.Main");

        } catch (ClassNotFoundException e) {
            System.out.println("❌ ERROR: Driver MySQL no encontrado");
            System.out.println("Solución: Verifica que mysql-connector-java-8.0.33.jar esté en com/lib/");
        } catch (SQLException e) {
            System.out.println("❌ ERROR DE CONEXIÓN: " + e.getMessage());
            System.out.println("Código: " + e.getErrorCode());
            System.out.println("Estado: " + e.getSQLState());

            if (e.getMessage().contains("Unknown database")) {
                System.out.println("\n💡 SOLUCIÓN: Ejecuta primero CreateDatabase.java");
                System.out.println("   java -cp \".;com/lib/mysql-connector-java-8.0.33.jar\" CreateDatabase");
            } else if (e.getMessage().contains("Communications link")) {
                System.out.println("\n💡 SOLUCIÓN: MySQL Server no está ejecutándose");
            } else if (e.getMessage().contains("Access denied")) {
                System.out.println("\n💡 SOLUCIÓN: Verifica usuario/contraseña");
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR GENERAL: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== FIN DEL TEST FINAL ===");
    }
}