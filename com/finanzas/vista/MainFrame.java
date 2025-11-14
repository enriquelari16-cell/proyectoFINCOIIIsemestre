package finanzas.vista;

import finanzas.controlador.FinanzasController;
import finanzas.modelo.Transaccion;
import finanzas.modelo.Usuario;
import finanzas.modelo.Meta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {

    private FinanzasController controlador;
    private Usuario usuarioActual;

    // Componentes principales
    private JTable tablaTransacciones;
    private DefaultTableModel modeloTabla;
    private JTable tablaMetas;
    private DefaultTableModel modeloTablaMetas;

    // Paneles
    private JPanel panelPrincipal;
    private JPanel panelTransacciones;
    private JPanel panelMetas;
    private JPanel panelEstadisticas;
    private JPanel panelConsejos;

    // Controles de filtro
    private JComboBox<String> comboTipoFiltro;
    private JTextField txtMontoMinimo;
    private JButton btnFiltrar, btnNuevaTransaccion, btnNuevaMeta;
    private JButton btnReportes, btnGestionarMetas;

    // Labels informativos
    private JLabel lblSaldoActual;
    private JLabel lblEstadisticas;
    private JTextArea txtAreaConsejos;

    // Pestañas
    private JTabbedPane tabbedPane;

public MainFrame(FinanzasController controlador) {
    this.controlador = controlador;
    this.usuarioActual = controlador.getUsuarioActual();

    if (usuarioActual == null) {
        JOptionPane.showMessageDialog(this,
                "Error: No se encontró el usuario autenticado.",
                "Error de sesión",
                JOptionPane.ERROR_MESSAGE);
        dispose();
        return;
    }

    initComponents();
    cargarDatos();

    setTitle("Gestor Financiero Educativo - " + usuarioActual.getNombre());
    setSize(1400, 900);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);

    addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            controlador.cerrarSesion();
        }
    });
}

    private void initComponents() {
        // Crear pestañas principales
        tabbedPane = new JTabbedPane();

        // Panel de Transacciones
        crearPanelTransacciones();
        tabbedPane.addTab("Transacciones", panelTransacciones);

        // Panel de Metas
        crearPanelMetas();
        tabbedPane.addTab("Metas Financieras", panelMetas);

        // Panel de Estadísticas
        crearPanelEstadisticas();
        tabbedPane.addTab("Estadísticas", panelEstadisticas);

        // Panel de Consejos Educativos
        crearPanelConsejos();
        tabbedPane.addTab("Consejos", panelConsejos);

        // Panel superior con información del usuario
        JPanel panelSuperior = new JPanel(new BorderLayout());
        lblSaldoActual = new JLabel("Saldo Actual: $0.00", JLabel.CENTER);
        lblSaldoActual.setFont(new Font("Arial", Font.BOLD, 16));
        lblSaldoActual.setForeground(new Color(0, 150, 0));
        panelSuperior.add(lblSaldoActual, BorderLayout.CENTER);

        // Layout general
        setLayout(new BorderLayout());
        add(panelSuperior, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void crearPanelTransacciones() {
        panelTransacciones = new JPanel(new BorderLayout());

        // Panel de controles
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT));

        comboTipoFiltro = new JComboBox<>(new String[]{"Todos", "Ingreso", "Gasto"});
        txtMontoMinimo = new JTextField(8);
        btnFiltrar = new JButton("Filtrar");
        btnNuevaTransaccion = new JButton("Nueva Transacción");
        btnNuevaTransaccion.setBackground(new Color(50, 150, 100));
        btnNuevaTransaccion.setForeground(Color.WHITE);

        panelControles.add(new JLabel("Tipo:"));
        panelControles.add(comboTipoFiltro);
        panelControles.add(new JLabel("Monto mínimo:"));
        panelControles.add(txtMontoMinimo);
        panelControles.add(btnFiltrar);
        panelControles.add(btnNuevaTransaccion);

        // Tabla de transacciones
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Tipo", "Monto", "Descripción", "Fecha"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaTransacciones = new JTable(modeloTabla);
        tablaTransacciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTabla = new JScrollPane(tablaTransacciones);

        // Panel inferior con estadísticas rápidas
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblEstadisticas = new JLabel("Estadísticas de gastos últimos 30 días");
        panelInferior.add(lblEstadisticas);

        panelTransacciones.add(panelControles, BorderLayout.NORTH);
        panelTransacciones.add(scrollTabla, BorderLayout.CENTER);
        panelTransacciones.add(panelInferior, BorderLayout.SOUTH);

        // Eventos
        btnFiltrar.addActionListener(e -> cargarTransacciones());
        btnNuevaTransaccion.addActionListener(e -> {
            NuevaTransaccionDialog dialog = new NuevaTransaccionDialog(this, controlador, usuarioActual.getId());
            dialog.setVisible(true);
            cargarDatos();
        });
    }

    private void crearPanelMetas() {
        panelMetas = new JPanel(new BorderLayout());

        // Panel de controles
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNuevaMeta = new JButton("Nueva Meta");
        btnNuevaMeta.setBackground(new Color(100, 100, 200));
        btnNuevaMeta.setForeground(Color.WHITE);
        btnGestionarMetas = new JButton("Gestionar Metas");

        panelControles.add(btnNuevaMeta);
        panelControles.add(btnGestionarMetas);

        // Tabla de metas
        modeloTablaMetas = new DefaultTableModel(new String[]{"ID", "Nombre", "Objetivo", "Ahorrado", "Progreso", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaMetas = new JTable(modeloTablaMetas);
        JScrollPane scrollTabla = new JScrollPane(tablaMetas);

        panelMetas.add(panelControles, BorderLayout.NORTH);
        panelMetas.add(scrollTabla, BorderLayout.CENTER);

        // Eventos
        btnNuevaMeta.addActionListener(e -> {
            // Implementar diálogo para nueva meta
            JOptionPane.showMessageDialog(this, "Funcionalidad de nueva meta próximamente");
        });

        btnGestionarMetas.addActionListener(e -> {
            GestionarMetasDialog dialog = new GestionarMetasDialog(this, controlador);
            dialog.setVisible(true);
            cargarMetas();
        });
    }

    private void crearPanelEstadisticas() {
        panelEstadisticas = new JPanel(new BorderLayout());

        JTextArea txtAreaEstadisticas = new JTextArea();
        txtAreaEstadisticas.setEditable(false);
        txtAreaEstadisticas.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollEstadisticas = new JScrollPane(txtAreaEstadisticas);

        JButton btnActualizarEstadisticas = new JButton("Actualizar Estadísticas");
        btnActualizarEstadisticas.addActionListener(e -> {
            Map<String, Object> estadisticas = controlador.obtenerEstadisticasGenerales();
            txtAreaEstadisticas.setText(generarReporteEstadisticas(estadisticas));
        });

        panelEstadisticas.add(btnActualizarEstadisticas, BorderLayout.NORTH);
        panelEstadisticas.add(scrollEstadisticas, BorderLayout.CENTER);
    }

    private void crearPanelConsejos() {
        panelConsejos = new JPanel(new BorderLayout());

        // Usar el panel educativo especializado
        EducationalTipsPanel panelEducativo = new EducationalTipsPanel(controlador);
        panelConsejos.add(panelEducativo, BorderLayout.CENTER);

        // Agregar un panel inferior con información adicional
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblInfo = new JLabel("<html><i>💡 Los consejos se actualizan automáticamente basados en tus hábitos financieros</i></html>");
        lblInfo.setForeground(Color.GRAY);
        panelInferior.add(lblInfo);

        panelConsejos.add(panelInferior, BorderLayout.SOUTH);
    }

    // ========== MÉTODOS PARA CARGAR DATOS ==========

    private void cargarDatos() {
        cargarSaldoActual();
        cargarTransacciones();
        cargarMetas();
        cargarEstadisticas();
        cargarConsejos();
    }

    private void cargarSaldoActual() {
        if (usuarioActual != null) {
            lblSaldoActual.setText(String.format("Saldo Actual: $%.2f",
                    usuarioActual.getPresupuestoActual()));
        }
    }

    private void cargarTransacciones() {
        String tipo = (String) comboTipoFiltro.getSelectedItem();
        double montoMin = 0;
        try {
            montoMin = Double.parseDouble(txtMontoMinimo.getText());
            if (montoMin < 0) montoMin = 0;
        } catch (NumberFormatException ex) {
            montoMin = 0;
        }

        List<Transaccion> lista = controlador.obtenerTransaccionesFiltradas(tipo, montoMin);
        modeloTabla.setRowCount(0); // Limpiar tabla

        for (Transaccion t : lista) {
            modeloTabla.addRow(new Object[]{
                    t.getId(),
                    t.getTipo(),
                    String.format("$%.2f", t.getMonto()),
                    t.getDescripcion(),
                    t.getFecha().toString()
            });
        }
    }

    private void cargarMetas() {
        List<Meta> metas = controlador.obtenerMetasUsuario();
        modeloTablaMetas.setRowCount(0);

        for (Meta meta : metas) {
            String estado = meta.isCompleta() ? "Completada" : "En progreso";
            double progreso = meta.getProgreso();

            modeloTablaMetas.addRow(new Object[]{
                    meta.getId(),
                    meta.getNombre(),
                    String.format("$%.2f", meta.getMontoObjetivo()),
                    String.format("$%.2f", meta.getAhorroActual()),
                    String.format("%.1f%%", progreso),
                    estado
            });
        }
    }

    private void cargarEstadisticas() {
        LocalDate hoy = LocalDate.now();
        LocalDate haceUnMes = hoy.minusMonths(1);

        // Usar el DAO directamente para estadísticas específicas de gastos
        Map<String, Object> estadisticasGastos = controlador.obtenerEstadisticasGenerales();

        if (estadisticasGastos != null && !estadisticasGastos.isEmpty()) {
            double totalGastos = (double) estadisticasGastos.getOrDefault("totalGastos", 0.0);
            int numeroGastos = (int) estadisticasGastos.getOrDefault("numeroGastos", 0);
            double promedio = (double) estadisticasGastos.getOrDefault("gastoPromedio", 0.0);

            lblEstadisticas.setText(String.format("Gastos últimos 30 días - Total: $%.2f, Número: %d, Promedio: $%.2f",
                    totalGastos, numeroGastos, promedio));
        } else {
            lblEstadisticas.setText("No hay datos de gastos para los últimos 30 días.");
        }
    }

    private void cargarConsejos() {
        // Los consejos se cargan automáticamente en el EducationalTipsPanel
        // No necesitamos hacer nada aquí ya que el panel se actualiza solo
        // El txtAreaConsejos no existe en este contexto, se maneja en EducationalTipsPanel
    }

    private String generarReporteEstadisticas(Map<String, Object> estadisticas) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE DE ESTADÍSTICAS FINANCIERAS ===\n\n");

        reporte.append(String.format("Saldo Actual: $%.2f\n", usuarioActual.getPresupuestoActual()));
        reporte.append(String.format("Presupuesto Inicial: $%.2f\n\n", usuarioActual.getPresupuestoInicial()));

        reporte.append("TRANSACCIONES:\n");
        reporte.append(String.format("- Total de transacciones: %d\n",
                (int) estadisticas.getOrDefault("totalTransacciones", 0)));
        reporte.append(String.format("- Total ingresos: $%.2f\n",
                (double) estadisticas.getOrDefault("totalIngresos", 0.0)));
        reporte.append(String.format("- Total gastos: $%.2f\n",
                (double) estadisticas.getOrDefault("totalGastos", 0.0)));
        reporte.append(String.format("- Balance general: $%.2f\n\n",
                (double) estadisticas.getOrDefault("balanceGeneral", 0.0)));

        reporte.append("METAS FINANCIERAS:\n");
        reporte.append(String.format("- Total de metas: %d\n",
                (int) estadisticas.getOrDefault("totalMetas", 0)));
        reporte.append(String.format("- Metas completadas: %d\n",
                (int) estadisticas.getOrDefault("metasCompletadas", 0)));
        reporte.append(String.format("- Metas pendientes: %d\n",
                (int) estadisticas.getOrDefault("metasPendientes", 0)));
        reporte.append(String.format("- Progreso promedio: %.1f%%\n",
                (double) estadisticas.getOrDefault("progresoPromedio", 0.0)));

        return reporte.toString();
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