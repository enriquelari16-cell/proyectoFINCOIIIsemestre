package finanzas.vista;

import finanzas.modelo.Transaccion;
import finanzas.modelo.Usuario;
import finanzas.dao.ConexionDB;
import finanzas.dao.TransaccionDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HistorialFrame extends JFrame {
    private Usuario usuarioActual;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cbTipo;
    private JComboBox<String> cbCategoria;
    private JTextField txtCantidad;
    private TransaccionDAO transaccionDAO;

    public HistorialFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        this.transaccionDAO = new TransaccionDAO();
        initComponents();
        cargarHistorial();
    }

    private void initComponents() {
        setTitle("Historial de Transacciones - " + usuarioActual.getNombre());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout());
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));

        // Filtro por tipo
        panelFiltros.add(new JLabel("Tipo:"));
        cbTipo = new JComboBox<>(new String[]{"Todos", "Ingreso", "Gasto"});
        panelFiltros.add(cbTipo);

        // Filtro por categoría
        panelFiltros.add(new JLabel("Categoría:"));
        cbCategoria = new JComboBox<>();
        cbCategoria.addItem("Todas");
        cargarCategorias();
        panelFiltros.add(cbCategoria);

        // Cantidad de registros
        panelFiltros.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField("50", 5);
        panelFiltros.add(txtCantidad);

        // Botón filtrar
        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> cargarHistorial());
        panelFiltros.add(btnFiltrar);

        // Tabla de historial
        String[] columnas = {"ID", "Categoría", "Monto", "Tipo", "Fecha", "Descripción"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tablaHistorial);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnCerrar = new JButton("Cerrar");

        btnActualizar.addActionListener(e -> cargarHistorial());
        btnCerrar.addActionListener(e -> dispose());

        panelBotones.add(btnActualizar);
        panelBotones.add(btnCerrar);

        // Agregar componentes al panel principal
        panelPrincipal.add(panelFiltros, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void cargarCategorias() {
        try {
            String sql = "SELECT nombre FROM categorias WHERE usuario_id = ?";
            try (Connection conn = ConexionDB.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, usuarioActual.getId());
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    cbCategoria.addItem(rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar categorías: " + e.getMessage());
        }
    }

    private void cargarHistorial() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);

        try {
            String tipoFiltro = cbTipo.getSelectedItem().toString();
            String categoriaFiltro = cbCategoria.getSelectedItem().toString();
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());

            List<Transaccion> transacciones = obtenerHistorial(
                    usuarioActual.getId(),
                    tipoFiltro.equals("Todos") ? null : tipoFiltro,
                    categoriaFiltro.equals("Todas") ? 0 : obtenerCategoriaId(categoriaFiltro),
                    cantidad
            );

            // Llenar tabla
            for (Transaccion t : transacciones) {
                Object[] fila = {
                        t.getId(),
                        obtenerNombreCategoria(t.getCategoriaId()),
                        String.format("$%.2f", t.getMonto()),
                        t.getTipo(),
                        t.getFecha().toString(),
                        t.getDescripcion()
                };
                modeloTabla.addRow(fila);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido");
            txtCantidad.setText("50");
        }
    }

    public List<Transaccion> obtenerHistorial(int usuarioId, String tipo, int categoriaId, int limite) {
        List<Transaccion> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t.*, c.nombre as categoria_nombre FROM transacciones t ");
        sql.append("JOIN categorias c ON t.categoria_id = c.id ");
        sql.append("WHERE c.usuario_id = ?");

        if (tipo != null && !tipo.isEmpty()) {
            sql.append(" AND t.tipo = ?");
        }
        if (categoriaId > 0) {
            sql.append(" AND t.categoria_id = ?");
        }

        sql.append(" ORDER BY t.fecha DESC, t.id DESC");
        if (limite > 0) {
            sql.append(" LIMIT ?");
        }

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            stmt.setInt(paramIndex++, usuarioId);

            if (tipo != null && !tipo.isEmpty()) {
                stmt.setString(paramIndex++, tipo);
            }
            if (categoriaId > 0) {
                stmt.setInt(paramIndex++, categoriaId);
            }
            if (limite > 0) {
                stmt.setInt(paramIndex++, limite);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Transaccion t = new Transaccion();
                t.setId(rs.getInt("id"));
                t.setCategoriaId(rs.getInt("categoria_id"));
                t.setMonto(rs.getDouble("monto"));
                t.setTipo(rs.getString("tipo"));

                // Convertir fecha de String a LocalDate
                Date fechaSQL = rs.getDate("fecha");
                if (fechaSQL != null) {
                    t.setFecha(fechaSQL.toLocalDate());
                }

                t.setDescripcion(rs.getString("descripcion"));
                lista.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener historial: " + e.getMessage());
        }

        return lista;
    }

    private int obtenerCategoriaId(String nombreCategoria) {
        try {
            String sql = "SELECT id FROM categorias WHERE nombre = ? AND usuario_id = ?";
            try (Connection conn = ConexionDB.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, nombreCategoria);
                stmt.setInt(2, usuarioActual.getId());

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String obtenerNombreCategoria(int categoriaId) {
        try {
            String sql = "SELECT nombre FROM categorias WHERE id = ?";
            try (Connection conn = ConexionDB.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, categoriaId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getString("nombre");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Desconocida";
    }
}