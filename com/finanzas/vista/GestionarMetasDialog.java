package finanzas.vista;

import finanzas.modelo.Usuario;
import finanzas.modelo.Meta;
import finanzas.dao.MetaDAO;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class GestionarMetasDialog extends JDialog {
    private Usuario usuario;
    private MetaDAO metaDAO;
    private JTable tableMetas;
    private DefaultTableModel tableModel;
    private JTextField txtNombre, txtMontoObjetivo, txtAhorroActual;
    private JTextArea txtDescripcion;

    public GestionarMetasDialog(JFrame parent, Usuario usuario) {
        super(parent, "Gestionar Metas", true);
        this.usuario = usuario;
        this.metaDAO = new MetaDAO();

        setSize(700, 500);
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
        panel.setBorder(BorderFactory.createTitledBorder("Mis Metas"));

        String[] columnas = {"ID", "Nombre", "Monto Objetivo", "Ahorro Actual", "Progreso %", "Descripción"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableMetas = new JTable(tableModel);
        tableMetas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ocultar columna ID
        tableMetas.getColumnModel().getColumn(0).setMinWidth(0);
        tableMetas.getColumnModel().getColumn(0).setMaxWidth(0);

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
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
                return;
            }

            Meta meta = new Meta();
            meta.setUsuarioId(usuario.getId());
            meta.setNombre(nombre);
            meta.setMontoObjetivo(montoObjetivo);
            meta.setAhorroActual(ahorroActual);
            meta.setDescripcion(descripcion);

            if (metaDAO.crearMeta(meta)) {
                JOptionPane.showMessageDialog(this, "Meta agregada exitosamente");
                limpiarFormulario();
                cargarMetas();
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar la meta");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos");
        }
    }

    private void actualizarAhorro(ActionEvent e) {
        int selectedRow = tableMetas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una meta");
            return;
        }

        try {
            int metaId = (Integer) tableModel.getValueAt(selectedRow, 0);
            String input = JOptionPane.showInputDialog(this, "Ingrese el nuevo monto de ahorro:");

            if (input != null && !input.trim().isEmpty()) {
                double nuevoAhorro = Double.parseDouble(input);

                if (metaDAO.actualizarAhorro(metaId, nuevoAhorro)) {
                    JOptionPane.showMessageDialog(this, "Ahorro actualizado");
                    cargarMetas();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un valor numérico válido");
        }
    }

    private void eliminarMeta(ActionEvent e) {
        int selectedRow = tableMetas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una meta");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar esta meta?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int metaId = (Integer) tableModel.getValueAt(selectedRow, 0);

            if (metaDAO.eliminarMeta(metaId)) {
                JOptionPane.showMessageDialog(this, "Meta eliminada");
                cargarMetas();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar");
            }
        }
    }

    private void cargarMetas() {
        tableModel.setRowCount(0);
        List<Meta> metas = metaDAO.obtenerMetasPorUsuario(usuario.getId());

        for (Meta meta : metas) {
            double progreso = (meta.getAhorroActual() / meta.getMontoObjetivo()) * 100;
            Object[] fila = {
                    meta.getId(),
                    meta.getNombre(),
                    String.format("$%.2f", meta.getMontoObjetivo()),
                    String.format("$%.2f", meta.getAhorroActual()),
                    String.format("%.1f%%", progreso),
                    meta.getDescripcion()
            };
            tableModel.addRow(fila);
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtMontoObjetivo.setText("");
        txtAhorroActual.setText("");
        txtDescripcion.setText("");
    }
}