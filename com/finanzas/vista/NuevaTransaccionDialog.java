package com.finanzas.vista;

import com.finanzas.controlador.FinanzasController;
import com.finanzas.modelo.Transaccion;
import com.finanzas.modelo.Categoria;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class NuevaTransaccionDialog extends JDialog {

    private FinanzasController controlador;
    private int usuarioId;

    private JComboBox<String> comboTipo;
    private JComboBox<Categoria> comboCategoria;
    private JTextField txtMonto;
    private JTextField txtDescripcion;
    private JButton btnGuardar, btnCancelar;

    public NuevaTransaccionDialog(Frame parent, FinanzasController controlador, int usuarioId) {
        super(parent, "Nueva Transacción", true);
        this.controlador = controlador;
        this.usuarioId = usuarioId;

        initComponentes();

        setSize(400, 300);
        setLocationRelativeTo(parent);
    }

    private void initComponentes() {
        comboTipo = new JComboBox<>(new String[]{"Ingreso", "Gasto"});
        comboCategoria = new JComboBox<>();
        txtMonto = new JTextField(10);
        txtDescripcion = new JTextField(20);
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        // Configurar colores
        btnGuardar.setBackground(new Color(50, 150, 100));
        btnGuardar.setForeground(Color.WHITE);
        btnCancelar.setBackground(new Color(200, 50, 50));
        btnCancelar.setForeground(Color.WHITE);

        // Cargar categorías (por ahora vacío, se puede implementar después)
        comboCategoria.addItem(new Categoria(0, "Sin categoría", 0));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tipo
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        panel.add(comboTipo, gbc);

        // Categoría
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 1;
        panel.add(comboCategoria, gbc);

        // Monto
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Monto:"), gbc);
        gbc.gridx = 1;
        panel.add(txtMonto, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        panel.add(txtDescripcion, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(panelBotones, gbc);

        add(panel);

        btnGuardar.addActionListener(e -> guardarTransaccion());
        btnCancelar.addActionListener(e -> dispose());

        // Agregar validación en tiempo real
        txtMonto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                validarMonto();
            }
        });
    }

    private void validarMonto() {
        try {
            double monto = Double.parseDouble(txtMonto.getText());
            if (monto <= 0) {
                txtMonto.setBackground(Color.PINK);
            } else {
                txtMonto.setBackground(Color.WHITE);
            }
        } catch (NumberFormatException e) {
            txtMonto.setBackground(Color.PINK);
        }
    }

    private void guardarTransaccion() {
        try {
            String tipo = (String) comboTipo.getSelectedItem();
            double monto = Double.parseDouble(txtMonto.getText());
            String descripcion = txtDescripcion.getText().trim();

            // Validaciones
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor a cero", "Error", JOptionPane.ERROR_MESSAGE);
                txtMonto.requestFocus();
                return;
            }

            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La descripción no puede estar vacía", "Error", JOptionPane.ERROR_MESSAGE);
                txtDescripcion.requestFocus();
                return;
            }

            // Obtener saldo actual del controlador
            double saldoActual = controlador.getUsuarioActual().getPresupuestoActual();

            // Calcular nuevo saldo
            double nuevoSaldo;
            if ("Ingreso".equals(tipo)) {
                nuevoSaldo = saldoActual + monto;
            } else { // Gasto
                nuevoSaldo = saldoActual - monto;

                // Verificar fondos suficientes
                if (nuevoSaldo < 0) {
                    int respuesta = JOptionPane.showConfirmDialog(this,
                            String.format("Esta transacción dejará su saldo en negativo ($%.2f).\n¿Desea continuar?",
                                    nuevoSaldo),
                            "Saldo Insuficiente",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (respuesta == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
            }

            // Crear transacción
            Transaccion transaccion = new Transaccion();
            transaccion.setUsuarioId(usuarioId);
            transaccion.setTipo(tipo);
            transaccion.setMonto(monto);
            transaccion.setDescripcion(descripcion);
            transaccion.setFecha(LocalDate.now());
            transaccion.setSaldoDespues(nuevoSaldo);

            // Obtener categoría seleccionada si existe
            Categoria categoriaSeleccionada = (Categoria) comboCategoria.getSelectedItem();
            if (categoriaSeleccionada != null && categoriaSeleccionada.getId() > 0) {
                transaccion.setCategoriaId(categoriaSeleccionada.getId());
            }

            // Guardar transacción usando el controlador
            if (controlador.crearTransaccion(transaccion)) {
                JOptionPane.showMessageDialog(this,
                        String.format("✅ Transacción creada correctamente.\nNuevo saldo: $%.2f", nuevoSaldo),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                // Mostrar consejo educativo si es un gasto alto
                if ("Gasto".equals(tipo) && monto > saldoActual * 0.1) {
                    JOptionPane.showMessageDialog(this,
                            "💡 Consejo: Este gasto representa más del 10% de tu saldo actual.\n" +
                            "Considera si es una compra necesaria o si puedes esperar.",
                            "Consejo Financiero",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear la transacción", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Monto inválido. Use solo números.", "Error", JOptionPane.ERROR_MESSAGE);
            txtMonto.requestFocus();
        }
    }

    private double obtenerSaldoActualUsuario() {
        try {
            // You'll need to add this method to your UsuarioDAO
            // For now, we'll use a simple query approach
            String sql = "SELECT presupuesto_actual FROM usuarios WHERE id = ?";

            try (java.sql.Connection conn = com.finanzas.dao.ConexionDB.getConnection();
                 java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, usuarioId);
                java.sql.ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getDouble("presupuesto_actual");
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener saldo actual: " + e.getMessage());
        }
        return -1; // Error indicator
    }
}