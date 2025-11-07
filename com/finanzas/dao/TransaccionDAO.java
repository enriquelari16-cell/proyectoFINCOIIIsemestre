package finanzas.dao;

import finanzas.modelo.Transaccion;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class TransaccionDAO {
    private static final String[] TIPOS_COMPARACION_VALIDOS = {"MENSUAL", "TRIMESTRAL", "ANUAL"};

    public boolean crearTransaccion(Transaccion transaccion) {
        String sql = "INSERT INTO transacciones (usuario_id, tipo, monto, descripcion, fecha, saldo_despues) VALUES (?, ?, ?, ?, ?, ?)";

        // Validar datos antes de insertar
        if (transaccion == null) {
            System.err.println("Error: La transacción es null");
            return false;
        }

        if (transaccion.getUsuarioId() <= 0) {
            System.err.println("Error: ID de usuario inválido: " + transaccion.getUsuarioId());
            return false;
        }

        if (transaccion.getTipo() == null || transaccion.getTipo().trim().isEmpty()) {
            System.err.println("Error: Tipo de transacción inválido");
            return false;
        }

        if (transaccion.getMonto() <= 0) {
            System.err.println("Error: Monto inválido: " + transaccion.getMonto());
            return false;
        }

        if (transaccion.getFecha() == null) {
            System.err.println("Error: Fecha es null");
            return false;
        }

        try (Connection conn = ConexionDB.getConnection()) {

            if (conn == null) {
                System.err.println("Error: No se pudo obtener conexión a la base de datos");
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                // Debug: mostrar los valores que se van a insertar
                System.out.println("Insertando transacción:");
                System.out.println("- Usuario ID: " + transaccion.getUsuarioId());
                System.out.println("- Tipo: " + transaccion.getTipo());
                System.out.println("- Monto: " + transaccion.getMonto());
                System.out.println("- Descripción: " + transaccion.getDescripcion());
                System.out.println("- Fecha: " + transaccion.getFecha());
                System.out.println("- Saldo después: " + transaccion.getSaldoDespues());

                stmt.setInt(1, transaccion.getUsuarioId());
                stmt.setString(2, transaccion.getTipo());
                stmt.setDouble(3, transaccion.getMonto());
                stmt.setString(4, transaccion.getDescripcion());
                stmt.setDate(5, Date.valueOf(transaccion.getFecha()));
                stmt.setDouble(6, transaccion.getSaldoDespues());

                int filasAfectadas = stmt.executeUpdate();
                System.out.println("Filas afectadas: " + filasAfectadas);

                if (filasAfectadas > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            transaccion.setId(rs.getInt(1));
                            System.out.println("Transacción creada con ID: " + transaccion.getId());
                        }
                    }
                    return true;
                } else {
                    System.err.println("Error: No se insertaron filas");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al crear transacción: " + e.getMessage());
            System.err.println("Código de error SQL: " + e.getErrorCode());
            System.err.println("Estado SQL: " + e.getSQLState());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Error general al crear transacción: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public List<Map<String, Object>> obtenerDatosMensuales(int usuarioId, int año) {
        List<Map<String, Object>> datosMensuales = new ArrayList<>();
        String sql = "SELECT " +
                "MONTH(fecha) as mes, " +
                "MONTHNAME(fecha) as nombre_mes, " +
                "COALESCE(SUM(CASE WHEN tipo = 'Ingreso' THEN monto ELSE 0 END), 0) as ingresos, " +
                "COALESCE(SUM(CASE WHEN tipo = 'Gasto' THEN monto ELSE 0 END), 0) as gastos, " +
                "COUNT(*) as total_transacciones " +
                "FROM transacciones " +
                "WHERE usuario_id = ? AND YEAR(fecha) = ? " +
                "GROUP BY MONTH(fecha), MONTHNAME(fecha) " +
                "ORDER BY MONTH(fecha)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.setInt(2, año);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("mes", rs.getInt("mes"));
                    datos.put("nombreMes", rs.getString("nombre_mes"));
                    datos.put("ingresos", rs.getDouble("ingresos"));
                    datos.put("gastos", rs.getDouble("gastos"));
                    datos.put("balance", rs.getDouble("ingresos") - rs.getDouble("gastos"));
                    datos.put("totalTransacciones", rs.getInt("total_transacciones"));
                    datosMensuales.add(datos);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener datos mensuales: " + e.getMessage());
        }
        return datosMensuales;
    }

    public Map<String, Object> obtenerEstadisticasGastos(int usuarioId, LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> estadisticas = new HashMap<>();
        if (fechaInicio.isAfter(fechaFin)) {
            return estadisticas;
        }

        String sql = "SELECT " +
                "COUNT(*) as numero_gastos, " +
                "COALESCE(SUM(monto), 0) as total_gastos, " +
                "COALESCE(AVG(monto), 0) as gasto_promedio, " +
                "COALESCE(MIN(monto), 0) as gasto_minimo, " +
                "COALESCE(MAX(monto), 0) as gasto_maximo " +
                "FROM transacciones " +
                "WHERE usuario_id = ? AND tipo = 'Gasto' AND fecha BETWEEN ? AND ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.setDate(2, Date.valueOf(fechaInicio));
            stmt.setDate(3, Date.valueOf(fechaFin));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estadisticas.put("numeroGastos", rs.getInt("numero_gastos"));
                    estadisticas.put("totalGastos", rs.getDouble("total_gastos"));
                    estadisticas.put("gastoPromedio", rs.getDouble("gasto_promedio"));
                    estadisticas.put("gastoMinimo", rs.getDouble("gasto_minimo"));
                    estadisticas.put("gastoMaximo", rs.getDouble("gasto_maximo"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas de gastos: " + e.getMessage());
        }
        return estadisticas;
    }

    public Map<String, Object> obtenerEstadisticasGenerales(int usuarioId) {
        Map<String, Object> estadisticas = new HashMap<>();
        String sql = "SELECT " +
                "COUNT(*) as total_transacciones, " +
                "COALESCE(SUM(CASE WHEN tipo = 'Ingreso' THEN monto ELSE 0 END), 0) as total_ingresos, " +
                "COALESCE(SUM(CASE WHEN tipo = 'Gasto' THEN monto ELSE 0 END), 0) as total_gastos, " +
                "COALESCE(AVG(CASE WHEN tipo = 'Ingreso' THEN monto ELSE NULL END), 0) as promedio_ingresos, " +
                "COALESCE(AVG(CASE WHEN tipo = 'Gasto' THEN monto ELSE NULL END), 0) as promedio_gastos " +
                "FROM transacciones " +
                "WHERE usuario_id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estadisticas.put("totalTransacciones", rs.getInt("total_transacciones"));
                    estadisticas.put("totalIngresos", rs.getDouble("total_ingresos"));
                    estadisticas.put("totalGastos", rs.getDouble("total_gastos"));
                    estadisticas.put("promedioIngresos", rs.getDouble("promedio_ingresos"));
                    estadisticas.put("promedioGastos", rs.getDouble("promedio_gastos"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas generales: " + e.getMessage());
        }
        return estadisticas;
    }

    public List<Map<String, Object>> obtenerComparativoIngresosGastos(int usuarioId, String tipoComparacion, int año) {
        List<Map<String, Object>> comparativo = new ArrayList<>();

        if (!Arrays.asList(TIPOS_COMPARACION_VALIDOS).contains(tipoComparacion.toUpperCase())) {
            return comparativo;
        }

        String sql = construirQueryComparativo(tipoComparacion);

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            stmt.setInt(paramIndex++, usuarioId);
            if (!tipoComparacion.equalsIgnoreCase("ANUAL")) {
                stmt.setInt(paramIndex, año);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("periodo", rs.getInt("periodo"));
                    datos.put("nombrePeriodo", rs.getString("nombre_periodo"));
                    double ingresos = rs.getDouble("ingresos");
                    double gastos = rs.getDouble("gastos");
                    double total = ingresos + gastos;

                    datos.put("ingresos", ingresos);
                    datos.put("gastos", gastos);
                    datos.put("balance", ingresos - gastos);
                    datos.put("porcentajeIngresos", total > 0 ? (ingresos / total) * 100 : 0.0);
                    datos.put("porcentajeGastos", total > 0 ? (gastos / total) * 100 : 0.0);
                    comparativo.add(datos);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener comparativo: " + e.getMessage());
        }
        return comparativo;
    }

    private String construirQueryComparativo(String tipoComparacion) {
        switch (tipoComparacion.toUpperCase()) {
            case "MENSUAL":
                return "SELECT " +
                        "MONTH(fecha) as periodo, " +
                        "MONTHNAME(fecha) as nombre_periodo, " +
                        "COALESCE(SUM(CASE WHEN tipo = 'Ingreso' THEN monto ELSE 0 END), 0) as ingresos, " +
                        "COALESCE(SUM(CASE WHEN tipo = 'Gasto' THEN monto ELSE 0 END), 0) as gastos " +
                        "FROM transacciones " +
                        "WHERE usuario_id = ? AND YEAR(fecha) = ? " +
                        "GROUP BY MONTH(fecha), MONTHNAME(fecha) " +
                        "ORDER BY MONTH(fecha)";
            case "TRIMESTRAL":
                return "SELECT " +
                        "QUARTER(fecha) as periodo, " +
                        "CONCAT('Q', QUARTER(fecha)) as nombre_periodo, " +
                        "COALESCE(SUM(CASE WHEN tipo = 'Ingreso' THEN monto ELSE 0 END), 0) as ingresos, " +
                        "COALESCE(SUM(CASE WHEN tipo = 'Gasto' THEN monto ELSE 0 END), 0) as gastos " +
                        "FROM transacciones " +
                        "WHERE usuario_id = ? AND YEAR(fecha) = ? " +
                        "GROUP BY QUARTER(fecha) " +
                        "ORDER BY QUARTER(fecha)";
            case "ANUAL":
                return "SELECT " +
                        "YEAR(fecha) as periodo, " +
                        "YEAR(fecha) as nombre_periodo, " +
                        "COALESCE(SUM(CASE WHEN tipo = 'Ingreso' THEN monto ELSE 0 END), 0) as ingresos, " +
                        "COALESCE(SUM(CASE WHEN tipo = 'Gasto' THEN monto ELSE 0 END), 0) as gastos " +
                        "FROM transacciones " +
                        "WHERE usuario_id = ? " +
                        "GROUP BY YEAR(fecha) " +
                        "ORDER BY YEAR(fecha)";
            default:
                return "";
        }
    }



    public List<Transaccion> obtenerTransaccionesPorUsuario(int usuarioId, String tipoFiltro, double montoMinimo) {
        List<Transaccion> transacciones = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM transacciones WHERE usuario_id = ?");

        if (!"Todos".equals(tipoFiltro)) {
            sql.append(" AND tipo = ?");
        }
        if (montoMinimo > 0) {
            sql.append(" AND monto >= ?");
        }
        sql.append(" ORDER BY fecha DESC, id DESC");

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            stmt.setInt(paramIndex++, usuarioId);

            if (!"Todos".equals(tipoFiltro)) {
                stmt.setString(paramIndex++, tipoFiltro);
            }
            if (montoMinimo > 0) {
                stmt.setDouble(paramIndex++, montoMinimo);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaccion t = new Transaccion();
                    t.setId(rs.getInt("id"));
                    t.setUsuarioId(rs.getInt("usuario_id"));
                    t.setTipo(rs.getString("tipo"));
                    t.setMonto(rs.getDouble("monto"));
                    t.setDescripcion(rs.getString("descripcion"));
                    t.setFecha(rs.getDate("fecha").toLocalDate());
                    t.setSaldoDespues(rs.getDouble("saldo_despues"));
                    transacciones.add(t);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener transacciones: " + e.getMessage());
        }
        return transacciones;
    }

    public boolean eliminarTransaccion(int transaccionId) {
        String sql = "DELETE FROM transacciones WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transaccionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar transacción: " + e.getMessage());
        }
        return false;
    }
}