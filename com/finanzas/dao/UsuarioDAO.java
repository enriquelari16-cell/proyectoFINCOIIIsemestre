package finanzas.dao;

import finanzas.modelo.Usuario;
import java.sql.*;
import java.time.LocalDateTime;

public class UsuarioDAO {

    // Verificar si existe un usuario por nombre
    public boolean existeUsuario(String nombre) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE nombre = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre.trim());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar si existe usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Registrar un nuevo usuario
    public boolean registrarUsuario(Usuario usuario) {
        System.out.println("Registrando usuario:");
        System.out.println("Nombre: " + usuario.getNombre());
        System.out.println("Edad: " + usuario.getEdad());
        System.out.println("Tipo de uso: " + usuario.getTipoUso());
        System.out.println("Presupuesto inicial: " + usuario.getPresupuestoInicial());
        System.out.println("Presupuesto actual: " + usuario.getPresupuestoActual());

        String sql = "INSERT INTO usuarios (nombre, edad, tipo_uso, contrasena, presupuesto_inicial, presupuesto_actual, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setByte(2, usuario.getEdad());
            stmt.setString(3, usuario.getTipoUso());
            stmt.setString(4, usuario.getContrasena()); // En producción, hashear la contraseña
            stmt.setDouble(5, usuario.getPresupuestoInicial());
            stmt.setDouble(6, usuario.getPresupuestoActual());
            stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                    usuario.setFechaCreacion(LocalDateTime.now());
                }
                System.out.println("Usuario registrado exitosamente con ID: " + usuario.getId());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Autenticar usuario
    public Usuario autenticarUsuario(String nombre, String contrasena) {
        System.out.println("Intentando autenticar:");
        System.out.println("Nombre recibido: [" + nombre + "]");
        System.out.println("Contraseña recibida: [" + contrasena + "]");

        String sql = "SELECT * FROM usuarios WHERE nombre = ? AND contrasena = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre.trim());
            stmt.setString(2, contrasena.trim());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Usuario autenticado exitosamente.");

                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setEdad(rs.getByte("edad"));
                usuario.setTipoUso(rs.getString("tipo_uso"));
                usuario.setContrasena(rs.getString("contrasena")); // Solo para uso interno
                usuario.setPresupuestoInicial(rs.getDouble("presupuesto_inicial"));
                usuario.setPresupuestoActual(rs.getDouble("presupuesto_actual"));

                // Manejar fecha_creacion si no es null
                Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
                if (fechaCreacion != null) {
                    usuario.setFechaCreacion(fechaCreacion.toLocalDateTime());
                }

                return usuario;
            } else {
                System.out.println("No se encontró coincidencia en la base de datos.");
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error de conexión a la base de datos", e);
        }
        return null;
    }

    // Obtener presupuesto actual de un usuario
    public double obtenerPresupuestoActual(int usuarioId) {
        String sql = "SELECT presupuesto_actual FROM usuarios WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("presupuesto_actual");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener presupuesto actual: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // Indicador de error
    }

    // Actualizar presupuesto actual
    public boolean actualizarPresupuesto(int usuarioId, double nuevoPresupuesto) {
        String sql = "UPDATE usuarios SET presupuesto_actual = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, nuevoPresupuesto);
            stmt.setInt(2, usuarioId);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Presupuesto actualizado para usuario ID: " + usuarioId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar presupuesto: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Obtener usuario completo por ID
    public Usuario obtenerUsuarioPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setEdad(rs.getByte("edad"));
                usuario.setTipoUso(rs.getString("tipo_uso"));
                usuario.setPresupuestoInicial(rs.getDouble("presupuesto_inicial"));
                usuario.setPresupuestoActual(rs.getDouble("presupuesto_actual"));

                Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
                if (fechaCreacion != null) {
                    usuario.setFechaCreacion(fechaCreacion.toLocalDateTime());
                }

                return usuario;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Actualizar información del usuario (sin contraseña)
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, edad = ?, tipo_uso = ?, presupuesto_inicial = ?, presupuesto_actual = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setByte(2, usuario.getEdad());
            stmt.setString(3, usuario.getTipoUso());
            stmt.setDouble(4, usuario.getPresupuestoInicial());
            stmt.setDouble(5, usuario.getPresupuestoActual());
            stmt.setInt(6, usuario.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Cambiar contraseña
    public boolean cambiarContrasena(int usuarioId, String nuevaContrasena) {
        String sql = "UPDATE usuarios SET contrasena = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevaContrasena); // En producción, hashear la contraseña
            stmt.setInt(2, usuarioId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar contraseña: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}