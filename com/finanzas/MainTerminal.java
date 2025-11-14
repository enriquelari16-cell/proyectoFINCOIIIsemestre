package finanzas;

import java.sql.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Versión terminal simplificada del gestor financiero educativo.
 * Interfaz de línea de comandos completa e interactiva.
 */
public class MainTerminal {
    private static Scanner scanner = new Scanner(System.in);
    private static Connection connection = null;
    private static int usuarioId = 0;
    private static String usuarioNombre = "";
    private static double saldoActual = 0;
    private static boolean enSesion = false;

    public static void main(String[] args) {
        // Conectar a la base de datos
        if (!conectarBaseDatos()) {
            System.out.println("❌ No se pudo conectar a la base de datos.");
            System.out.println("💡 Asegúrate de que MySQL esté ejecutándose.");
            return;
        }

        System.out.println("✅ Conectado a la base de datos.");
        
        mostrarBienvenida();
        
        while (true) {
            if (!enSesion) {
                mostrarMenuLogin();
            } else {
                mostrarMenuPrincipal();
            }
        }
    }

    /**
     * Conectar a la base de datos MySQL
     */
    private static boolean conectarBaseDatos() {
        try {
            // Intentar cargar el driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("? Usando MySQL Connector/J moderno");
            
            // Conectar
            String url = "jdbc:mysql://localhost:3306/finanzas_personales";
            String usuario = "ander";
            String contrasena = "";
            
            connection = DriverManager.getConnection(url, usuario, contrasena);
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver MySQL no encontrado.");
            return false;
        } catch (SQLException e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
            return false;
        }
    }

    /**
     * Mostrar el mensaje de bienvenida
     */
    private static void mostrarBienvenida() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("    💰 GESTOR FINANCIERO EDUCATIVO - TERMINAL 💰");
        System.out.println("=".repeat(60));
        System.out.println("¡Bienvenido a tu asistente financiero personal!");
        System.out.println();
        System.out.println("Características disponibles:");
        System.out.println("• 📊 Registro de ingresos y gastos");
        System.out.println("• 🎯 Gestión de metas financieras");
        System.out.println("• 📈 Estadísticas y reportes");
        System.out.println("• 💡 Consejos financieros educativos");
        System.out.println("• 🗂️ Categorización de gastos");
        System.out.println();
        System.out.println("¡Comienza tu viaje hacia la libertad financiera!");
        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * Menú de login y registro
     */
    private static void mostrarMenuLogin() {
        System.out.println("\n--- MENÚ DE ACCESO ---");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Crear nueva cuenta");
        System.out.println("3. Salir");
        System.out.print("Selecciona una opción: ");

        int opcion = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        switch (opcion) {
            case 1:
                iniciarSesion();
                break;
            case 2:
                crearCuenta();
                break;
            case 3:
                System.out.println("¡Gracias por usar el Gestor Financiero!");
                System.exit(0);
                break;
            default:
                System.out.println("❌ Opción inválida.");
        }
    }

    /**
     * Proceso de inicio de sesión
     */
    private static void iniciarSesion() {
        System.out.println("\n--- INICIO DE SESIÓN ---");
        System.out.print("Usuario: ");
        String nombre = scanner.nextLine();
        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine();

        try {
            String sql = "SELECT * FROM usuarios WHERE nombre = ? AND contrasena = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, nombre);
            stmt.setString(2, contrasena);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usuarioId = rs.getInt("id");
                usuarioNombre = rs.getString("nombre");
                saldoActual = rs.getDouble("presupuesto_actual");
                enSesion = true;
                System.out.println("✅ ¡Bienvenido, " + usuarioNombre + "!");
                System.out.printf("💰 Saldo actual: $%.2f\n", saldoActual);
            } else {
                System.out.println("❌ Credenciales incorrectas.");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("❌ Error al iniciar sesión: " + e.getMessage());
        }
    }

    /**
     * Proceso de creación de cuenta
     */
    private static void crearCuenta() {
        System.out.println("\n--- CREAR NUEVA CUENTA ---");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Edad: ");
        byte edad = scanner.nextByte();
        scanner.nextLine();
        System.out.print("Tipo de uso (Personal/Familiar/Empresarial): ");
        String tipoUso = scanner.nextLine();
        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine();
        System.out.print("Presupuesto inicial: $");
        double presupuesto = scanner.nextDouble();
        scanner.nextLine();

        try {
            String sql = "INSERT INTO usuarios (nombre, edad, tipo_uso, contrasena, presupuesto_inicial, presupuesto_actual, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, NOW())";
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, nombre);
            stmt.setByte(2, edad);
            stmt.setString(3, tipoUso);
            stmt.setString(4, contrasena);
            stmt.setDouble(5, presupuesto);
            stmt.setDouble(6, presupuesto);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                System.out.println("✅ ¡Cuenta creada exitosamente!");
                System.out.println("💡 Ya puedes iniciar sesión con tus credenciales.");
            } else {
                System.out.println("❌ Error al crear la cuenta.");
            }

            stmt.close();
        } catch (SQLException e) {
            System.out.println("❌ Error al crear la cuenta: " + e.getMessage());
        }
    }

    /**
     * Menú principal de la aplicación
     */
    private static void mostrarMenuPrincipal() {
        System.out.println("\n" + "-".repeat(40));
        System.out.println("    MENÚ PRINCIPAL - " + usuarioNombre.toUpperCase());
        System.out.println("-".repeat(40));
        System.out.printf("💰 Saldo: $%.2f\n", saldoActual);
        System.out.println();
        System.out.println("1. 💰 Transacciones");
        System.out.println("2. 🎯 Metas Financieras");
        System.out.println("3. 📊 Estadísticas y Reportes");
        System.out.println("4. 💡 Consejos Financieros");
        System.out.println("5. 🔄 Cerrar Sesión");
        System.out.print("Selecciona una opción: ");

        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1:
                menuTransacciones();
                break;
            case 2:
                menuMetas();
                break;
            case 3:
                menuEstadisticas();
                break;
            case 4:
                menuConsejos();
                break;
            case 5:
                cerrarSesion();
                break;
            default:
                System.out.println("❌ Opción inválida.");
        }
    }

    /**
     * Menú de gestión de transacciones
     */
    private static void menuTransacciones() {
        System.out.println("\n--- GESTIÓN DE TRANSACCIONES ---");
        System.out.println("1. Nueva transacción");
        System.out.println("2. Ver todas las transacciones");
        System.out.println("3. Filtrar por tipo");
        System.out.println("4. Volver");
        System.out.print("Selecciona una opción: ");

        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1:
                crearTransaccion();
                break;
            case 2:
                verTransacciones();
                break;
            case 3:
                filtrarTransacciones();
                break;
            case 4:
                return;
            default:
                System.out.println("❌ Opción inválida.");
        }
    }

    /**
     * Crear nueva transacción
     */
    private static void crearTransaccion() {
        System.out.println("\n--- NUEVA TRANSACCIÓN ---");
        System.out.println("Tipo: 1. Ingreso  2. Gasto");
        System.out.print("Selecciona: ");
        int tipo = scanner.nextInt();
        scanner.nextLine();
        
        String tipoTransaccion = (tipo == 1) ? "Ingreso" : "Gasto";
        
        System.out.print("Monto: $");
        double monto = scanner.nextDouble();
        scanner.nextLine();
        
        System.out.print("Descripción: ");
        String descripcion = scanner.nextLine();

        // Calcular nuevo saldo
        double nuevoSaldo = saldoActual;
        if (tipoTransaccion.equals("Ingreso")) {
            nuevoSaldo += monto;
        } else {
            nuevoSaldo -= monto;
            
            // Advertencia para gastos altos
            if (monto > saldoActual * 0.1) {
                System.out.println("\n⚠️ ADVERTENCIA: Este gasto representa más del 10% de tu saldo.");
                System.out.print("¿Estás seguro? (s/n): ");
                String confirmacion = scanner.nextLine();
                if (!confirmacion.equalsIgnoreCase("s")) {
                    return;
                }
            }
        }

        try {
            // Insertar transacción
            String sql = "INSERT INTO transacciones (usuario_id, tipo, monto, descripcion, fecha, saldo_despues) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            stmt.setString(2, tipoTransaccion);
            stmt.setDouble(3, monto);
            stmt.setString(4, descripcion);
            stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setDouble(6, nuevoSaldo);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                // Actualizar saldo del usuario
                String sqlSaldo = "UPDATE usuarios SET presupuesto_actual = ? WHERE id = ?";
                PreparedStatement stmtSaldo = connection.prepareStatement(sqlSaldo);
                stmtSaldo.setDouble(1, nuevoSaldo);
                stmtSaldo.setInt(2, usuarioId);
                stmtSaldo.executeUpdate();
                stmtSaldo.close();

                saldoActual = nuevoSaldo;
                System.out.println("✅ ¡Transacción registrada exitosamente!");
                System.out.printf("💰 Nuevo saldo: $%.2f\n", saldoActual);
                
                // Mostrar consejo educativo
                if (tipoTransaccion.equals("Gasto") && monto > saldoActual * 0.1) {
                    System.out.println("\n💡 CONSEJO EDUCATIVO:");
                    System.out.println("Este gasto representa más del 10% de tu saldo actual.");
                    System.out.println("Considera si es una compra necesaria o si puedes esperar.");
                }
            } else {
                System.out.println("❌ Error al registrar la transacción.");
            }

            stmt.close();
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Ver todas las transacciones
     */
    private static void verTransacciones() {
        System.out.println("\n--- MIS TRANSACCIONES ---");
        
        try {
            String sql = "SELECT * FROM transacciones WHERE usuario_id = ? ORDER BY fecha DESC, id DESC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("📝 No hay transacciones registradas.");
                return;
            }

            System.out.println("ID | Tipo   | Monto     | Descripción           | Fecha");
            System.out.println("-".repeat(60));
            
            while (rs.next()) {
                System.out.printf("%2d | %-7s | $%8.2f | %-20s | %s\n",
                        rs.getInt("id"), 
                        rs.getString("tipo"), 
                        rs.getDouble("monto"), 
                        rs.getString("descripcion"),
                        rs.getDate("fecha").toString());
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Filtrar transacciones
     */
    private static void filtrarTransacciones() {
        System.out.println("\n--- FILTRAR TRANSACCIONES ---");
        System.out.println("1. Solo ingresos");
        System.out.println("2. Solo gastos");
        System.out.print("Selecciona: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();
        
        String filtro = (opcion == 1) ? "Ingreso" : "Gasto";
        
        try {
            String sql = "SELECT * FROM transacciones WHERE usuario_id = ? AND tipo = ? ORDER BY fecha DESC, id DESC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            stmt.setString(2, filtro);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("📝 No hay transacciones de tipo " + filtro + ".");
                return;
            }

            System.out.println("\n--- " + filtro.toUpperCase() + "S ---");
            System.out.println("ID | Monto     | Descripción           | Fecha");
            System.out.println("-".repeat(50));
            
            while (rs.next()) {
                System.out.printf("%2d | $%8.2f | %-20s | %s\n",
                        rs.getInt("id"),
                        rs.getDouble("monto"),
                        rs.getString("descripcion"),
                        rs.getDate("fecha").toString());
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Menú de metas financieras
     */
    private static void menuMetas() {
        System.out.println("\n--- METAS FINANCIERAS ---");
        System.out.println("1. Nueva meta");
        System.out.println("2. Ver mis metas");
        System.out.println("3. Actualizar ahorro");
        System.out.println("4. Volver");
        System.out.print("Selecciona una opción: ");

        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1:
                crearMeta();
                break;
            case 2:
                verMetas();
                break;
            case 3:
                actualizarAhorro();
                break;
            case 4:
                return;
            default:
                System.out.println("❌ Opción inválida.");
        }
    }

    /**
     * Crear nueva meta
     */
    private static void crearMeta() {
        System.out.println("\n--- NUEVA META ---");
        System.out.print("Nombre de la meta: ");
        String nombre = scanner.nextLine();
        System.out.print("Monto objetivo: $");
        double objetivo = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Ahorro inicial: $");
        double ahorro = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Descripción: ");
        String descripcion = scanner.nextLine();

        try {
            String sql = "INSERT INTO metas (usuario_id, nombre, monto_objetivo, ahorro_actual, descripcion) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            stmt.setString(2, nombre);
            stmt.setDouble(3, objetivo);
            stmt.setDouble(4, ahorro);
            stmt.setString(5, descripcion);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                System.out.println("✅ ¡Meta creada exitosamente!");
                double progreso = (ahorro / objetivo) * 100;
                System.out.printf("📊 Progreso: %.1f%%\n", progreso);
            } else {
                System.out.println("❌ Error al crear la meta.");
            }

            stmt.close();
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Ver metas del usuario
     */
    private static void verMetas() {
        System.out.println("\n--- MIS METAS ---");
        
        try {
            String sql = "SELECT * FROM metas WHERE usuario_id = ? ORDER BY id DESC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("📝 No hay metas configuradas.");
                return;
            }

            System.out.println("ID | Nombre             | Objetivo   | Ahorrado  | Progreso | Estado");
            System.out.println("-".repeat(70));
            
            while (rs.next()) {
                double objetivo = rs.getDouble("monto_objetivo");
                double ahorro = rs.getDouble("ahorro_actual");
                double progreso = (ahorro / objetivo) * 100;
                String estado = ahorro >= objetivo ? "✅ Completada" : "🔄 En progreso";
                
                System.out.printf("%2d | %-18s | $%8.2f | $%8.2f | %6.1f%% | %s\n",
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        objetivo,
                        ahorro,
                        progreso,
                        estado);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Actualizar ahorro de una meta
     */
    private static void actualizarAhorro() {
        System.out.println("\n--- ACTUALIZAR AHORRO ---");
        System.out.print("ID de la meta: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Nuevo monto de ahorro: $");
        double nuevoAhorro = scanner.nextDouble();
        scanner.nextLine();

        try {
            String sql = "UPDATE metas SET ahorro_actual = ? WHERE id = ? AND usuario_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDouble(1, nuevoAhorro);
            stmt.setInt(2, id);
            stmt.setInt(3, usuarioId);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                System.out.println("✅ Ahorro actualizado.");
                
                // Verificar si la meta se completó
                sql = "SELECT monto_objetivo FROM metas WHERE id = ? AND usuario_id = ?";
                stmt = connection.prepareStatement(sql);
                stmt.setInt(1, id);
                stmt.setInt(2, usuarioId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next() && nuevoAhorro >= rs.getDouble("monto_objetivo")) {
                    System.out.println("🎉 ¡FELICITACIONES! Has completado esta meta.");
                }
                
                rs.close();
            } else {
                System.out.println("❌ Error al actualizar el ahorro.");
            }

            stmt.close();
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Menú de estadísticas y reportes
     */
    private static void menuEstadisticas() {
        System.out.println("\n--- ESTADÍSTICAS Y REPORTES ---");
        
        try {
            // Contar transacciones
            String sqlTrans = "SELECT COUNT(*) as total FROM transacciones WHERE usuario_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sqlTrans);
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int totalTrans = rs.getInt("total");
            
            // Calcular ingresos y gastos
            sqlTrans = "SELECT " +
                      "COALESCE(SUM(CASE WHEN tipo = 'Ingreso' THEN monto ELSE 0 END), 0) as total_ingresos, " +
                      "COALESCE(SUM(CASE WHEN tipo = 'Gasto' THEN monto ELSE 0 END), 0) as total_gastos " +
                      "FROM transacciones WHERE usuario_id = ?";
            stmt = connection.prepareStatement(sqlTrans);
            stmt.setInt(1, usuarioId);
            rs = stmt.executeQuery();
            rs.next();
            double totalIngresos = rs.getDouble("total_ingresos");
            double totalGastos = rs.getDouble("total_gastos");
            
            // Contar metas
            sqlTrans = "SELECT COUNT(*) as total_metas FROM metas WHERE usuario_id = ?";
            stmt = connection.prepareStatement(sqlTrans);
            stmt.setInt(1, usuarioId);
            rs = stmt.executeQuery();
            rs.next();
            int totalMetas = rs.getInt("total_metas");
            
            System.out.println("\n📊 RESUMEN FINANCIERO:");
            System.out.printf("💰 Saldo actual: $%.2f\n", saldoActual);
            System.out.printf("💵 Presupuesto inicial: %.2f\n", saldoActual + totalGastos - totalIngresos);
            
            System.out.println("\n💳 TRANSACCIONES:");
            System.out.printf("• Total de transacciones: %d\n", totalTrans);
            System.out.printf("• Total ingresos: $%.2f\n", totalIngresos);
            System.out.printf("• Total gastos: $%.2f\n", totalGastos);
            System.out.printf("• Balance: $%.2f\n", totalIngresos - totalGastos);
            
            System.out.println("\n🎯 METAS:");
            System.out.printf("• Total de metas: %d\n", totalMetas);
            
            if (totalMetas > 0) {
                sqlTrans = "SELECT " +
                          "SUM(CASE WHEN ahorro_actual >= monto_objetivo THEN 1 ELSE 0 END) as metas_completadas " +
                          "FROM metas WHERE usuario_id = ?";
                stmt = connection.prepareStatement(sqlTrans);
                stmt.setInt(1, usuarioId);
                rs = stmt.executeQuery();
                rs.next();
                int metasCompletadas = rs.getInt("metas_completadas");
                System.out.printf("• Metas completadas: %d\n", metasCompletadas);
                System.out.printf("• Metas pendientes: %d\n", totalMetas - metasCompletadas);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        
        System.out.print("\nPresiona Enter para continuar...");
        scanner.nextLine();
    }

    /**
     * Menú de consejos financieros
     */
    private static void menuConsejos() {
        System.out.println("\n--- CONSEJOS FINANCIEROS ---");
        
        List<String> consejos = obtenerConsejosEducativos();
        
        if (consejos.isEmpty()) {
            System.out.println("💡 ¡Registra algunas transacciones para recibir consejos personalizados!");
        } else {
            System.out.println("\n📚 TUS CONSEJOS PERSONALIZADOS:");
            for (int i = 0; i < consejos.size(); i++) {
                System.out.println((i + 1) + ". " + consejos.get(i));
            }
        }
        
        System.out.print("\nPresiona Enter para continuar...");
        scanner.nextLine();
    }

    /**
     * Generar consejos educativos basados en datos
     */
    private static List<String> obtenerConsejosEducativos() {
        List<String> consejos = new ArrayList<>();
        
        try {
            // Obtener estadísticas para generar consejos
            String sql = "SELECT " +
                        "COUNT(*) as total_trans, " +
                        "COALESCE(SUM(CASE WHEN tipo = 'Gasto' THEN monto ELSE 0 END), 0) as total_gastos, " +
                        "COALESCE(AVG(CASE WHEN tipo = 'Gasto' THEN monto ELSE NULL END), 0) as gasto_promedio " +
                        "FROM transacciones WHERE usuario_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            
            int totalTrans = rs.getInt("total_trans");
            double totalGastos = rs.getDouble("total_gastos");
            double gastoPromedio = rs.getDouble("gasto_promedio");
            
            // Consejos basados en patrones
            if (totalGastos > saldoActual * 0.8) {
                consejos.add("💡 Tus gastos están cerca del 80% de tu saldo. Considera reducir gastos innecesarios.");
            }
            
            if (gastoPromedio > 50) {
                consejos.add("⚠️ Tus gastos promedio son altos ($" + String.format("%.2f", gastoPromedio) + "). Revisa tus categorías de gasto.");
            }
            
            if (totalTrans < 5) {
                consejos.add("📝 Registra más transacciones para tener un mejor seguimiento de tus finanzas.");
            }
            
            // Verificar metas
            String sqlMetas = "SELECT COUNT(*) as total FROM metas WHERE usuario_id = ?";
            stmt = connection.prepareStatement(sqlMetas);
            stmt.setInt(1, usuarioId);
            rs = stmt.executeQuery();
            rs.next();
            int totalMetas = rs.getInt("total");
            
            if (totalMetas == 0) {
                consejos.add("🎯 Establece metas financieras para mantenerte motivado y organizado.");
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener consejos: " + e.getMessage());
        }
        
        return consejos;
    }

    /**
     * Cerrar sesión
     */
    private static void cerrarSesion() {
        usuarioId = 0;
        usuarioNombre = "";
        saldoActual = 0;
        enSesion = false;
        System.out.println("👋 ¡Sesión cerrada exitosamente!");
    }
}