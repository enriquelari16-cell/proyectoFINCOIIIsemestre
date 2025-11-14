package finanzas.dao;

import finanzas.modelo.Meta;
import java.sql.*;
import java.util.*;

public class MetaDAO {
    /**
     * Obtiene estadísticas generales de las metas de un usuario
     * @param meta ID del usuario
     * @return Mapa con estadísticas de metas
     */
    public boolean crearMeta(Meta meta) {
        String sql = "INSERT INTO metas (usuario_id, nombre, monto_objetivo, ahorro_actual, descripcion) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, meta.getUsuarioId());
            stmt.setString(2, meta.getNombre());
            stmt.setDouble(3, meta.getMontoObjetivo());
            stmt.setDouble(4, meta.getAhorroActual());
            stmt.setString(5, meta.getDescripcion());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    meta.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Meta> obtenerMetasPorUsuario(int usuarioId) {
        List<Meta> metas = new ArrayList<>();
        String sql = "SELECT * FROM metas WHERE usuario_id = ? ORDER BY id DESC";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Meta meta = new Meta();
                meta.setId(rs.getInt("id"));
                meta.setUsuarioId(rs.getInt("usuario_id"));
                meta.setNombre(rs.getString("nombre"));
                meta.setMontoObjetivo(rs.getDouble("monto_objetivo"));
                meta.setAhorroActual(rs.getDouble("ahorro_actual"));
                meta.setDescripcion(rs.getString("descripcion"));
                metas.add(meta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return metas;
    }

    public boolean actualizarAhorro(int metaId, double nuevoAhorro) {
        String sql = "UPDATE metas SET ahorro_actual = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, nuevoAhorro);
            stmt.setInt(2, metaId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminarMeta(int metaId) {
        String sql = "DELETE FROM metas WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, metaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, Object> obtenerEstadisticasMetas(int usuarioId) {
        Map<String, Object> estadisticas = new HashMap<>();
        String sql = "SELECT " +
                "COUNT(*) as total_metas, " +
                "SUM(CASE WHEN ahorro_actual >= monto_objetivo THEN 1 ELSE 0 END) as metas_completadas, " +
                "AVG((ahorro_actual / NULLIF(monto_objetivo, 0)) * 100) as progreso_promedio, " +
                "SUM(ahorro_actual) as total_ahorrado, " +
                "SUM(monto_objetivo) as total_objetivos " +
                "FROM metas WHERE usuario_id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                estadisticas.put("totalMetas", rs.getInt("total_metas"));
                estadisticas.put("metasCompletadas", rs.getInt("metas_completadas"));
                estadisticas.put("progresoPromedio", rs.getDouble("progreso_promedio"));
                estadisticas.put("totalAhorrado", rs.getDouble("total_ahorrado"));
                estadisticas.put("totalObjetivos", rs.getDouble("total_objetivos"));

                // Calcular metas pendientes
                int totalMetas = rs.getInt("total_metas");
                int metasCompletadas = rs.getInt("metas_completadas");
                estadisticas.put("metasPendientes", totalMetas - metasCompletadas);
            } else {
                // Si no hay metas, devolver valores por defecto
                estadisticas.put("totalMetas", 0);
                estadisticas.put("metasCompletadas", 0);
                estadisticas.put("progresoPromedio", 0.0);
                estadisticas.put("totalAhorrado", 0.0);
                estadisticas.put("totalObjetivos", 0.0);
                estadisticas.put("metasPendientes", 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // En caso de error, devolver valores por defecto
            estadisticas.put("totalMetas", 0);
            estadisticas.put("metasCompletadas", 0);
            estadisticas.put("progresoPromedio", 0.0);
            estadisticas.put("totalAhorrado", 0.0);
            estadisticas.put("totalObjetivos", 0.0);
            estadisticas.put("metasPendientes", 0);
        }

        return estadisticas;
    }
}