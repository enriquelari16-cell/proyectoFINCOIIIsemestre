package com.finanzas.vista;

import com.finanzas.modelo.Usuario;
import com.finanzas.modelo.Transaccion;
import com.finanzas.dao.TransaccionDAO;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VerHistorialDialog extends JDialog {
    private Usuario usuario;
    private TransaccionDAO transaccionDAO;
    private JTable tableHistorial;
    private DefaultTableModel tableModel;
    private JComboBox<String> comboFiltroTipo;
    private JTextField txtFiltroMonto;
    private JLabel lblTotalIngresos, lblTotalGastos, lblBalance;

    public VerHistorialDialog(JFrame parent, Usuario usuario) {
        super(parent, "Historial de Transacciones", true);
        this.usuario = usuario;
        this.transaccionDAO = new TransaccionDAO();

        setSize(800, 600);
        setLocationRelativeTo(parent);
        initUI();
        cargarHistorial();
        actualizarResumen();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Panel superior - Filtros
        JPanel panelFiltros = createFilterPanel();
        add(panelFiltros, BorderLayout.NORTH);

        // Panel central - Tabla
        JPanel panelTabla = createTablePanel();
        add(panelTabla, BorderLayout.CENTER);

        // Panel inferior - Resumen y botones
        JPanel panelInferior = createBottomPanel();
        add(panelInferior, BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Filtros"));

        panel.add(new JLabel("Tipo:"));
        comboFiltroTipo = new JComboBox<>(new String[]{"Todos", "Ingreso", "Gasto"});
        comboFiltroTipo.addActionListener(this::aplicarFiltros);
        panel.add(comboFiltroTipo);

        panel.add(new JLabel("Monto mínimo:"));
        txtFiltroMonto = new JTextField(8);
        panel.add(txtFiltroMonto);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(this::aplicarFiltros);
        panel.add(btnFiltrar);

        JButton btnLimpiar = new JButton("Limpiar Filtros");
        btnLimpiar.addActionListener(this::limpiarFiltros);
        panel.add(btnLimpiar);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Transacciones"));

        String[] columnas = {"ID", "Fecha", "Tipo", "Monto", "Descripción", "Saldo Después"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableHistorial = new JTable(tableModel);
        tableHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ocultar columna ID
        tableHistorial.getColumnModel().getColumn(0).setMinWidth(0);
        tableHistorial.getColumnModel().getColumn(0).setMaxWidth(0);

        // Personalizar renderizado de columnas
        tableHistorial.setDefaultRenderer(Object.class, new CustomTableCellRenderer());

        JScrollPane scrollPane = new JScrollPane(tableHistorial);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel de resumen
        JPanel panelResumen = new JPanel(new GridLayout(1, 3, 10, 5));
        panelResumen.setBorder(BorderFactory.createTitledBorder("Resumen"));

        lblTotalIngresos = new JLabel("Total Ingresos: $0.00");
        lblTotalIngresos.setForeground(new Color(0, 150, 0));
        lblTotalIngresos.setFont(lblTotalIngresos.getFont().deriveFont(Font.BOLD));

        lblTotalGastos = new JLabel("Total Gastos: $0.00");
        lblTotalGastos.setForeground(Color.RED);
        lblTotalGastos.setFont(lblTotalGastos.getFont().deriveFont(Font.BOLD));

        lblBalance = new JLabel("Balance: $0.00");
        lblBalance.setFont(lblBalance.getFont().deriveFont(Font.BOLD));

        panelResumen.add(lblTotalIngresos);
        panelResumen.add(lblTotalGastos);
        panelResumen.add(lblBalance);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnEliminar = new JButton("Eliminar Transacción");
        btnEliminar.addActionListener(this::eliminarTransaccion);

        JButton btnExportar = new JButton("Exportar a CSV");
        btnExportar.addActionListener(this::exportarCSV);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        panelBotones.add(btnEliminar);
        panelBotones.add(btnExportar);
        panelBotones.add(btnCerrar);

        panel.add(panelResumen, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private void aplicarFiltros(ActionEvent e) {
        String tipoFiltro = (String) comboFiltroTipo.getSelectedItem();
        String montoTexto = txtFiltroMonto.getText().trim();

        double montoMinimo = 0;
        if (!montoTexto.isEmpty()) {
            try {
                montoMinimo = Double.parseDouble(montoTexto);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un monto válido");
                return;
            }
        }

        cargarHistorialConFiltros(tipoFiltro, montoMinimo);
        actualizarResumen();
    }

    private void limpiarFiltros(ActionEvent e) {
        comboFiltroTipo.setSelectedIndex(0);
        txtFiltroMonto.setText("");
        cargarHistorial();
        actualizarResumen();
    }

    private void eliminarTransaccion(ActionEvent e) {
        int selectedRow = tableHistorial.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una transacción");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar esta transacción?\nEsto afectará su saldo actual.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int transaccionId = (Integer) tableModel.getValueAt(selectedRow, 0);

            if (transaccionDAO.eliminarTransaccion(transaccionId)) {
                JOptionPane.showMessageDialog(this, "Transacción eliminada");
                cargarHistorial();
                actualizarResumen();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la transacción");
            }
        }
    }

    private void exportarCSV(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("historial_transacciones.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile());

                // Escribir encabezados
                writer.append("Fecha,Tipo,Monto,Descripción,Saldo Después\n");

                // Escribir datos
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 1; j < tableModel.getColumnCount(); j++) { // Omitir ID
                        writer.append(tableModel.getValueAt(i, j).toString());
                        if (j < tableModel.getColumnCount() - 1) writer.append(",");
                    }
                    writer.append("\n");
                }

                writer.close();
                JOptionPane.showMessageDialog(this, "Historial exportado exitosamente");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage());
            }
        }
    }

    private void cargarHistorial() {
        cargarHistorialConFiltros("Todos", 0);
    }

    private void cargarHistorialConFiltros(String tipoFiltro, double montoMinimo) {
        tableModel.setRowCount(0);
        List<Transaccion> transacciones = transaccionDAO.obtenerTransaccionesPorUsuario(
                usuario.getId(), tipoFiltro, montoMinimo);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Transaccion t : transacciones) {
            Object[] fila = {
                    t.getId(),
                    t.getFecha().format(formatter),
                    t.getTipo(),
                    String.format("$%.2f", t.getMonto()),
                    t.getDescripcion(),
                    String.format("$%.2f", t.getSaldoDespues())
            };
            tableModel.addRow(fila);
        }
    }

    private void actualizarResumen() {
        double totalIngresos = 0;
        double totalGastos = 0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String tipo = (String) tableModel.getValueAt(i, 2);
            String montoStr = (String) tableModel.getValueAt(i, 3);
            double monto = Double.parseDouble(montoStr.replace("$", ""));

            if ("Ingreso".equals(tipo)) {
                totalIngresos += monto;
            } else {
                totalGastos += monto;
            }
        }

        double balance = totalIngresos - totalGastos;

        lblTotalIngresos.setText(String.format("Total Ingresos: $%.2f", totalIngresos));
        lblTotalGastos.setText(String.format("Total Gastos: $%.2f", totalGastos));
        lblBalance.setText(String.format("Balance: $%.2f", balance));
        lblBalance.setForeground(balance >= 0 ? new Color(0, 150, 0) : Color.RED);
    }

    // Clase interna para personalizar el renderizado de celdas
    private class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                String tipo = (String) table.getValueAt(row, 2);
                if ("Ingreso".equals(tipo)) {
                    c.setBackground(new Color(230, 255, 230));
                } else {
                    c.setBackground(new Color(255, 230, 230));
                }
            }

            return c;
        }
    }
}