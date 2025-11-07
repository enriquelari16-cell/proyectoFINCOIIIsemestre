package finanzas.controlador;

import finanzas.dao.*;
import finanzas.modelo.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador principal que maneja la lógica de negocio de la aplicación financiera.
 * Implementa el patrón MVC y utiliza colecciones Java para gestión de datos en memoria.
 */
public class FinanzasController {

    // DAOs para persistencia
    private UsuarioDAO usuarioDAO;
    private TransaccionDAO transaccionDAO;
    private MetaDAO metaDAO;

    // Colecciones en memoria para gestión rápida
    private Map<Integer, Usuario> usuariosCache;
    private Map<Integer, List<Transaccion>> transaccionesPorUsuario;
    private Map<Integer, List<Meta>> metasPorUsuario;
    private Map<Integer, List<Categoria>> categoriasPorUsuario;

    // Usuario actualmente logueado
    private Usuario usuarioActual;

    public FinanzasController() {
        this.usuarioDAO = new UsuarioDAO();
        this.transaccionDAO = new TransaccionDAO();
        this.metaDAO = new MetaDAO();

        // Inicializar colecciones
        this.usuariosCache = new HashMap<>();
        this.transaccionesPorUsuario = new HashMap<>();
        this.metasPorUsuario = new HashMap<>();
        this.categoriasPorUsuario = new HashMap<>();
    }

    // ========== GESTIÓN DE USUARIOS ==========

    /**
     * Registra un nuevo usuario en el sistema
     */
    public boolean registrarUsuario(Usuario usuario) {
        if (usuarioDAO.registrarUsuario(usuario)) {
            usuariosCache.put(usuario.getId(), usuario);
            return true;
        }
        return false;
    }

    /**
     * Autentica un usuario y lo establece como usuario actual
     */
    public boolean autenticarUsuario(String nombre, String contrasena) {
        Usuario usuario = usuarioDAO.autenticarUsuario(nombre, contrasena);
        if (usuario != null) {
            this.usuarioActual = usuario;
            usuariosCache.put(usuario.getId(), usuario);
            cargarDatosUsuario(usuario.getId());
            return true;
        }
        return false;
    }

    /**
     * Obtiene el usuario actualmente autenticado
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Cierra la sesión del usuario actual
     */
    public void cerrarSesion() {
        this.usuarioActual = null;
        // Limpiar datos sensibles de memoria
        transaccionesPorUsuario.clear();
        metasPorUsuario.clear();
        categoriasPorUsuario.clear();
    }

    // ========== GESTIÓN DE TRANSACCIONES ==========

    /**
     * Crea una nueva transacción y actualiza el presupuesto del usuario
     */
    public boolean crearTransaccion(Transaccion transaccion) {
        if (transaccionDAO.crearTransaccion(transaccion)) {
            // Actualizar presupuesto del usuario
            double nuevoPresupuesto = calcularNuevoPresupuesto(transaccion);
            usuarioDAO.actualizarPresupuesto(transaccion.getUsuarioId(), nuevoPresupuesto);

            // Actualizar cache
            transaccionesPorUsuario.computeIfAbsent(transaccion.getUsuarioId(), k -> new ArrayList<>())
                                  .add(transaccion);

            // Actualizar usuario en cache
            Usuario usuario = usuariosCache.get(transaccion.getUsuarioId());
            if (usuario != null) {
                usuario.setPresupuestoActual(nuevoPresupuesto);
            }

            return true;
        }
        return false;
    }

    /**
     * Obtiene transacciones filtradas del usuario actual
     */
    public List<Transaccion> obtenerTransaccionesFiltradas(String tipoFiltro, double montoMinimo) {
        if (usuarioActual == null) return new ArrayList<>();

        List<Transaccion> transacciones = transaccionesPorUsuario.getOrDefault(usuarioActual.getId(), new ArrayList<>());

        return transacciones.stream()
                .filter(t -> tipoFiltro.equals("Todos") || t.getTipo().equals(tipoFiltro))
                .filter(t -> t.getMonto() >= montoMinimo)
                .sorted((t1, t2) -> t2.getFecha().compareTo(t1.getFecha()))
                .collect(Collectors.toList());
    }

    /**
     * Elimina una transacción y actualiza el presupuesto
     */
    public boolean eliminarTransaccion(int transaccionId) {
        if (usuarioActual == null) return false;

        // Buscar la transacción
        List<Transaccion> transacciones = transaccionesPorUsuario.get(usuarioActual.getId());
        Transaccion transaccionAEliminar = null;

        if (transacciones != null) {
            for (Transaccion t : transacciones) {
                if (t.getId() == transaccionId) {
                    transaccionAEliminar = t;
                    break;
                }
            }
        }

        if (transaccionAEliminar != null && transaccionDAO.eliminarTransaccion(transaccionId)) {
            // Revertir el efecto en el presupuesto
            double ajustePresupuesto = transaccionAEliminar.isIngreso() ?
                -transaccionAEliminar.getMonto() : transaccionAEliminar.getMonto();
            double nuevoPresupuesto = usuarioActual.getPresupuestoActual() + ajustePresupuesto;

            usuarioDAO.actualizarPresupuesto(usuarioActual.getId(), nuevoPresupuesto);
            usuarioActual.setPresupuestoActual(nuevoPresupuesto);

            // Remover de cache
            transacciones.remove(transaccionAEliminar);

            return true;
        }
        return false;
    }

    // ========== GESTIÓN DE METAS ==========

    /**
     * Crea una nueva meta financiera
     */
    public boolean crearMeta(Meta meta) {
        if (metaDAO.crearMeta(meta)) {
            metasPorUsuario.computeIfAbsent(meta.getUsuarioId(), k -> new ArrayList<>())
                          .add(meta);
            return true;
        }
        return false;
    }

    /**
     * Obtiene todas las metas del usuario actual
     */
    public List<Meta> obtenerMetasUsuario() {
        if (usuarioActual == null) return new ArrayList<>();
        return metasPorUsuario.getOrDefault(usuarioActual.getId(), new ArrayList<>());
    }

    /**
     * Actualiza el ahorro de una meta
     */
    public boolean actualizarAhorroMeta(int metaId, double nuevoAhorro) {
        if (metaDAO.actualizarAhorro(metaId, nuevoAhorro)) {
            // Actualizar en cache
            List<Meta> metas = metasPorUsuario.get(usuarioActual.getId());
            if (metas != null) {
                for (Meta meta : metas) {
                    if (meta.getId() == metaId) {
                        meta.setAhorroActual(nuevoAhorro);
                        break;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Elimina una meta
     */
    public boolean eliminarMeta(int metaId) {
        if (metaDAO.eliminarMeta(metaId)) {
            // Remover de cache
            List<Meta> metas = metasPorUsuario.get(usuarioActual.getId());
            if (metas != null) {
                metas.removeIf(meta -> meta.getId() == metaId);
            }
            return true;
        }
        return false;
    }

    // ========== ESTADÍSTICAS Y REPORTES ==========

    /**
     * Obtiene estadísticas generales del usuario
     */
    public Map<String, Object> obtenerEstadisticasGenerales() {
        if (usuarioActual == null) return new HashMap<>();

        Map<String, Object> estadisticas = transaccionDAO.obtenerEstadisticasGenerales(usuarioActual.getId());

        // Agregar estadísticas de metas
        Map<String, Object> estadisticasMetas = metaDAO.obtenerEstadisticasMetas(usuarioActual.getId());
        estadisticas.putAll(estadisticasMetas);

        // Calcular balance general
        double ingresos = (double) estadisticas.getOrDefault("totalIngresos", 0.0);
        double gastos = (double) estadisticas.getOrDefault("totalGastos", 0.0);
        estadisticas.put("balanceGeneral", ingresos - gastos);

        return estadisticas;
    }

    /**
     * Obtiene consejos educativos basados en el comportamiento financiero
     */
    public List<String> obtenerConsejosEducativos() {
        List<String> consejos = new ArrayList<>();
        if (usuarioActual == null) return consejos;

        Map<String, Object> estadisticas = obtenerEstadisticasGenerales();

        double balance = (double) estadisticas.getOrDefault("balanceGeneral", 0.0);
        int totalTransacciones = (int) estadisticas.getOrDefault("totalTransacciones", 0);
        double promedioGastos = (double) estadisticas.getOrDefault("promedioGastos", 0.0);

        if (balance < 0) {
            consejos.add("💡 Tu balance es negativo. Considera reducir gastos innecesarios.");
        } else if (balance > usuarioActual.getPresupuestoInicial() * 0.1) {
            consejos.add("🎉 ¡Excelente! Estás ahorrando más del 10% de tu presupuesto inicial.");
        }

        if (promedioGastos > usuarioActual.getPresupuestoActual() * 0.3) {
            consejos.add("⚠️ Tus gastos promedio son altos. Revisa tus categorías de gasto.");
        }

        if (totalTransacciones < 5) {
            consejos.add("📝 Registra más transacciones para tener un mejor seguimiento.");
        }

        List<Meta> metas = obtenerMetasUsuario();
        long metasCompletadas = metas.stream().filter(Meta::isCompleta).count();
        if (metasCompletadas == 0 && !metas.isEmpty()) {
            consejos.add("🎯 Establece metas realistas y trabaja consistentemente hacia ellas.");
        }

        return consejos;
    }

    // ========== MÉTODOS PRIVADOS ==========

    /**
     * Calcula el nuevo presupuesto después de una transacción
     */
    private double calcularNuevoPresupuesto(Transaccion transaccion) {
        Usuario usuario = usuariosCache.get(transaccion.getUsuarioId());
        if (usuario == null) return 0;

        double presupuestoActual = usuario.getPresupuestoActual();
        if (transaccion.isIngreso()) {
            return presupuestoActual + transaccion.getMonto();
        } else {
            return presupuestoActual - transaccion.getMonto();
        }
    }

    /**
     * Carga los datos del usuario desde la base de datos a las colecciones en memoria
     */
    private void cargarDatosUsuario(int usuarioId) {
        // Cargar transacciones
        List<Transaccion> transacciones = transaccionDAO.obtenerTransaccionesPorUsuario(usuarioId, "Todos", 0);
        transaccionesPorUsuario.put(usuarioId, new ArrayList<>(transacciones));

        // Cargar metas
        List<Meta> metas = metaDAO.obtenerMetasPorUsuario(usuarioId);
        metasPorUsuario.put(usuarioId, new ArrayList<>(metas));

        // Aquí se podrían cargar categorías si existiera un DAO para ellas
        categoriasPorUsuario.put(usuarioId, new ArrayList<>());
    }
}