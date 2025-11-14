package com.finanzas.vista;

import com.finanzas.controlador.FinanzasController;
import com.finanzas.modelo.Meta;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class GestionarMetasDialog extends JDialog {
    private FinanzasController controlador;
    private JTable tableMetas;
    private DefaultTableModel tableModel;
    private JTextField txtNombre, txtMontoObjetivo, txtAhorroActual;
    private JTextArea txtDescripcion;

    public GestionarMetasDialog(JFrame parent, FinanzasController controlador) {
        super(parent, "Gestionar Metas Financieras", true);
        this.controlador = controlador;

        setSize(800, 600);
        setLocationRelativeTo(parent);
        initUI();
        cargarMetas();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Panel superior - Formulario
        JPanel panelFormulario = createFormPanel();
        add(panelFormulario, BorderLayout.NORTH);

        // Panel central - Tabla
        JPanel panelTabla = createTablePanel();
        add(panelTabla, BorderLayout.CENTER);

        // Panel inferior - Botones
        JPanel panelBotones = createButtonPanel();
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Nueva Meta"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        panel.add(txtNombre, gbc);

        // Monto Objetivo
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("Monto Objetivo:"), gbc);
        gbc.gridx = 3;
        txtMontoObjetivo = new JTextField(10);
        panel.add(txtMontoObjetivo, gbc);

        // Ahorro Actual
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Ahorro Actual:"), gbc);
        gbc.gridx = 1;
        txtAhorroActual = new JTextField(10);
        panel.add(txtAhorroActual, gbc);

        // Descripción
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 3;
        txtDescripcion = new JTextArea(2, 15);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        panel.add(scrollDesc, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Mis Metas Financieras"));

        String[] columnas = {"ID", "Nombre", "Objetivo", "Ahorrado", "Progreso", "Estado", "Descripción"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableMetas = new JTable(tableModel);
        tableMetas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableMetas.setRowHeight(25);

        // Configurar colores para filas completadas
        tableMetas.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Verificar si la meta está completada (columna 5 contiene el estado)
                Object estadoValue = table.getValueAt(row, 5);
                if (estadoValue != null && estadoValue.toString().contains("Completada")) {
                    setBackground(new java.awt.Color(200, 255, 200)); // Verde claro
                    setForeground(java.awt.Color.BLACK);
                } else {
                    setBackground(java.awt.Color.WHITE);
                    setForeground(java.awt.Color.BLACK);
                }

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }

                return this;
            }
        });

        // Ocultar columna ID
        tableMetas.getColumnModel().getColumn(0).setMinWidth(0);
        tableMetas.getColumnModel().getColumn(0).setMaxWidth(0);

        // Configurar anchos de columna
        tableMetas.getColumnModel().getColumn(1).setPreferredWidth(120); // Nombre
        tableMetas.getColumnModel().getColumn(2).setPreferredWidth(80);  // Objetivo
        tableMetas.getColumnModel().getColumn(3).setPreferredWidth(80);  // Ahorrado
        tableMetas.getColumnModel().getColumn(4).setPreferredWidth(70);  // Progreso
        tableMetas.getColumnModel().getColumn(5).setPreferredWidth(100); // Estado
        tableMetas.getColumnModel().getColumn(6).setPreferredWidth(200); // Descripción

        JScrollPane scrollPane = new JScrollPane(tableMetas);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton btnAgregar = new JButton("Agregar Meta");
        btnAgregar.addActionListener(this::agregarMeta);

        JButton btnActualizar = new JButton("Actualizar Ahorro");
        btnActualizar.addActionListener(this::actualizarAhorro);

        JButton btnEliminar = new JButton("Eliminar Meta");
        btnEliminar.addActionListener(this::eliminarMeta);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        panel.add(btnAgregar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);
        panel.add(btnCerrar);

        return panel;
    }

    private void agregarMeta(ActionEvent e) {
        try {
            String nombre = txtNombre.getText().trim();
            double montoObjetivo = Double.parseDouble(txtMontoObjetivo.getText());
            double ahorroActual = Double.parseDouble(txtAhorroActual.getText());
            String descripcion = txtDescripcion.getText().trim();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }

            if (montoObjetivo <= 0) {
                JOptionPane.showMessageDialog(this, "El monto objetivo debe ser mayor a cero", "Error", JOptionPane.ERROR_MESSAGE);
                txtMontoObjetivo.requestFocus();
                return;
            }

            if (ahorroActual < 0) {
                JOptionPane.showMessageDialog(this, "El ahorro actual no puede ser negativo", "Error", JOptionPane.ERROR_MESSAGE);
                txtAhorroActual.requestFocus();
                return;
            }

            Meta meta = new Meta();
            meta.setUsuarioId(controlador.getUsuarioActual().getId());
            meta.setNombre(nombre);
            meta.setMontoObjetivo(montoObjetivo);
            meta.setAhorroActual(ahorroActual);
            meta.setDescripcion(descripcion);

            if (controlador.crearMeta(meta)) {
                JOptionPane.showMessageDialog(this, "✅ Meta agregada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarMetas();

                // Mostrar consejo educativo
                if (montoObjetivo > controlador.getUsuarioActual().getPresupuestoActual() * 2) {
                    JOptionPane.showMessageDialog(this,
                            "💡 Consejo: Esta meta es ambiciosa. Considera dividir en metas más pequeñas y alcanzables.",
                            "Consejo Financiero",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar la meta", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarAhorro(ActionEvent e) {
        int selectedRow = tableMetas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una meta para actualizar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int metaId = (Integer) tableModel.getValueAt(selectedRow, 0);
            String nombreMeta = (String) tableModel.getValueAt(selectedRow, 1);
            String ahorroActual = ((String) tableModel.getValueAt(selectedRow, 3)).replace("$", "");

            String input = JOptionPane.showInputDialog(this,
                    String.format("Meta: %s\nAhorro actual: $%s\n\nIngrese el nuevo monto de ahorro:", nombreMeta, ahorroActual),
                    "Actualizar Ahorro",
                    JOptionPane.QUESTION_MESSAGE);

            if (input != null && !input.trim().isEmpty()) {
                double nuevoAhorro = Double.parseDouble(input.trim());

                if (nuevoAhorro < 0) {
                    JOptionPane.showMessageDialog(this, "El ahorro no puede ser negativo", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (controlador.actualizarAhorroMeta(metaId, nuevoAhorro)) {
                    JOptionPane.showMessageDialog(this, "✅ Ahorro actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarMetas();

                    // Verificar si la meta se completó
                    List<Meta> metas = controlador.obtenerMetasUsuario();
                    for (Meta meta : metas) {
                        if (meta.getId() == metaId && meta.isCompleta()) {
                            JOptionPane.showMessageDialog(this,
                                    String.format("🎉 ¡Felicitaciones! Has completado la meta '%s'", meta.getNombre()),
                                    "Meta Completada",
                                    JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar el ahorro", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un valor numérico válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarMeta(ActionEvent e) {
        int selectedRow = tableMetas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una meta para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombreMeta = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("¿Está seguro de eliminar la meta '%s'?\nEsta acción no se puede deshacer.", nombreMeta),
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int metaId = (Integer) tableModel.getValueAt(selectedRow, 0);

            if (controlador.eliminarMeta(metaId)) {
                JOptionPane.showMessageDialog(this, "✅ Meta eliminada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarMetas();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la meta", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarMetas() {
        tableModel.setRowCount(0);
        List<Meta> metas = controlador.obtenerMetasUsuario();

        for (Meta meta : metas) {
            double progreso = meta.getProgreso();
            String estado = meta.isCompleta() ? "✅ Completada" : "⏳ En progreso";

            Object[] fila = {
                    meta.getId(),
                    meta.getNombre(),
                    String.format("$%.2f", meta.getMontoObjetivo()),
                    String.format("$%.2f", meta.getAhorroActual()),
                    String.format("%.1f%%", progreso),
                    estado,
                    meta.getDescripcion()
            };
            tableModel.addRow(fila);
        }

        // Actualizar título de la tabla con estadísticas
        if (metas.isEmpty()) {
            setTitle("Gestionar Metas Financieras - No tienes metas registradas");
        } else {
            long completadas = metas.stream().filter(Meta::isCompleta).count();
            setTitle(String.format("Gestionar Metas Financieras - %d metas (%d completadas)",
                    metas.size(), completadas));
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtMontoObjetivo.setText("");
        txtAhorroActual.setText("");
        txtDescripcion.setText("");
    }
}