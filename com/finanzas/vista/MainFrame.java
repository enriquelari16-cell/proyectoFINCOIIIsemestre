package finanzas.vista;

import finanzas.dao.TransaccionDAO;
import finanzas.modelo.Transaccion;
import finanzas.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MainFrame extends JFrame {

    private TransaccionDAO transaccionDAO;
    private int usuarioId; // Por ejemplo, usuario actual con ID 1

    private JTable tablaTransacciones;
    private DefaultTableModel modeloTabla;

    private JComboBox<String> comboTipoFiltro;
    private JTextField txtMontoMinimo;
    private JButton btnFiltrar, btnNuevaTransaccion;

    private JLabel lblEstadisticas;

    public MainFrame(Usuario usuario) {
        this.usuarioId = usuario.getId();  // Extract the ID from the Usuario object
        transaccionDAO = new TransaccionDAO();  // Initialize your DAO properly

        initComponents(); // Fixed method name
        cargarTransacciones("Todos", 0); // Added default parameters
        cargarEstadisticas();

        setTitle("Gestion Financiera Personal");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Fixed method name from initComponentes to initComponents
    private void initComponents() {
        // Panel superior con filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));

        comboTipoFiltro = new JComboBox<>(new String[]{"Todos", "Ingreso", "Gasto"});
        txtMontoMinimo = new JTextField(8);
        btnFiltrar = new JButton("Filtrar");
        btnNuevaTransaccion = new JButton("Nueva Transacción");

        panelFiltros.add(new JLabel("Tipo:"));
        panelFiltros.add(comboTipoFiltro);
        panelFiltros.add(new JLabel("Monto mínimo:"));
        panelFiltros.add(txtMontoMinimo);
        panelFiltros.add(btnFiltrar);
        panelFiltros.add(btnNuevaTransaccion);

        // Tabla transacciones
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Tipo", "Monto", "Descripción", "Fecha", "Saldo Después"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // No editable
            }
        };
        tablaTransacciones = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaTransacciones);

        // Label estadísticas
        lblEstadisticas = new JLabel("Estadísticas de gastos últimos 30 días");

        // Layout general
        setLayout(new BorderLayout());
        add(panelFiltros, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        add(lblEstadisticas, BorderLayout.SOUTH);

        // Eventos
        btnFiltrar.addActionListener(e -> {
            String tipo = (String) comboTipoFiltro.getSelectedItem();
            double montoMin = 0;
            try {
                montoMin = Double.parseDouble(txtMontoMinimo.getText());
                if (montoMin < 0) montoMin = 0;
            } catch (NumberFormatException ex) {
                montoMin = 0;
            }
            cargarTransacciones(tipo, montoMin);
            cargarEstadisticas();
        });

        btnNuevaTransaccion.addActionListener(e -> {
            NuevaTransaccionDialog dialog = new NuevaTransaccionDialog(this, usuarioId);
            dialog.setVisible(true);
            // Luego de cerrar el diálogo, recargamos datos
            cargarTransacciones("Todos", 0);
            cargarEstadisticas();
        });
    }

    private void cargarTransacciones(String tipoFiltro, double montoMinimo) {
        List<Transaccion> lista = transaccionDAO.obtenerTransaccionesPorUsuario(usuarioId, tipoFiltro, montoMinimo);
        modeloTabla.setRowCount(0); // Limpiar tabla

        for (Transaccion t : lista) {
            modeloTabla.addRow(new Object[]{
                    t.getId(),
                    t.getTipo(),
                    t.getMonto(),
                    t.getDescripcion(),
                    t.getFecha(),
                    t.getSaldoDespues()
            });
        }
    }

    private void cargarEstadisticas() {
        LocalDate hoy = LocalDate.now();
        LocalDate haceUnMes = hoy.minusMonths(1);
        var estadisticas = transaccionDAO.obtenerEstadisticasGastos(usuarioId, haceUnMes, hoy);

        if (estadisticas != null && !estadisticas.isEmpty()) {
            double totalGastos = (double) estadisticas.getOrDefault("totalGastos", 0.0);
            int numeroGastos = (int) estadisticas.getOrDefault("numeroGastos", 0);
            double promedio = (double) estadisticas.getOrDefault("gastoPromedio", 0.0);

            lblEstadisticas.setText(String.format("Gastos últimos 30 días - Total: $%.2f, Número: %d, Promedio: $%.2f",
                    totalGastos, numeroGastos, promedio));
        } else {
            lblEstadisticas.setText("No hay datos de gastos para los últimos 30 días.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create a sample Usuario object for testing
            Usuario usuarioTest = new Usuario();
            usuarioTest.setId(1); // Set a test ID
            usuarioTest.setNombre("Usuario Test");

            new MainFrame(usuarioTest).setVisible(true);
        });
    }
}