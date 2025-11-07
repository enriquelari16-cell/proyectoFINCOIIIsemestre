package finanzas.vista;

import finanzas.dao.TransaccionDAO;
import finanzas.dao.UsuarioDAO;
import finanzas.modelo.Transaccion;
import finanzas.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class NuevaTransaccionDialog extends JDialog {

    private int usuarioId;
    private TransaccionDAO transaccionDAO;
    private UsuarioDAO usuarioDAO;

    private JComboBox<String> comboTipo;
    private JTextField txtMonto;
    private JTextField txtDescripcion;
    private JButton btnGuardar, btnCancelar;

    public NuevaTransaccionDialog(Frame parent, int usuarioId) {
        super(parent, "Nueva Transacción", true);
        this.usuarioId = usuarioId;
        transaccionDAO = new TransaccionDAO();
        usuarioDAO = new UsuarioDAO();

        initComponentes();

        setSize(350, 250);
        setLocationRelativeTo(parent);
    }

    private void initComponentes() {
        comboTipo = new JComboBox<>(new String[]{"Ingreso", "Gasto"});
        txtMonto = new JTextField(10);
        txtDescripcion = new JTextField(20);
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Tipo:"));
        panel.add(comboTipo);
        panel.add(new JLabel("Monto:"));
        panel.add(txtMonto);
        panel.add(new JLabel("Descripción:"));
        panel.add(txtDescripcion);
        panel.add(btnGuardar);
        panel.add(btnCancelar);

        add(panel);

        btnGuardar.addActionListener(e -> guardarTransaccion());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void guardarTransaccion() {
        try {
            String tipo = (String) comboTipo.getSelectedItem();
            double monto = Double.parseDouble(txtMonto.getText());
            String descripcion = txtDescripcion.getText();

            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor a cero", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get current user balance
            double saldoActual = obtenerSaldoActualUsuario();
            if (saldoActual == -1) {
                JOptionPane.showMessageDialog(this, "Error al obtener el saldo actual", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Calculate new balance
            double nuevoSaldo;
            if ("Ingreso".equals(tipo)) {
                nuevoSaldo = saldoActual + monto;
            } else { // Gasto
                nuevoSaldo = saldoActual - monto;

                // Optional: Check if user has sufficient funds
                if (nuevoSaldo < 0) {
                    int respuesta = JOptionPane.showConfirmDialog(this,
                            "Esta transacción dejará su saldo en negativo ($ " + String.format("%.2f", nuevoSaldo) +
                                    "). ¿Desea continuar?",
                            "Saldo Insuficiente",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (respuesta == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
            }

            // Create transaction
            Transaccion transaccion = new Transaccion();
            transaccion.setUsuarioId(usuarioId);
            transaccion.setTipo(tipo);
            transaccion.setMonto(monto);
            transaccion.setDescripcion(descripcion);
            transaccion.setFecha(LocalDate.now());
            transaccion.setSaldoDespues(nuevoSaldo);

            // Save transaction and update user balance
            boolean exitoTransaccion;
            if (transaccionDAO.crearTransaccion(transaccion)) exitoTransaccion = true;
            else exitoTransaccion = false;
            boolean exitoActualizacion = usuarioDAO.actualizarPresupuesto(usuarioId, nuevoSaldo);

            if (exitoTransaccion && exitoActualizacion) {
                JOptionPane.showMessageDialog(this,
                        String.format("Transacción creada correctamente.\nNuevo saldo: $%.2f", nuevoSaldo));
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear la transacción", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Monto inválido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double obtenerSaldoActualUsuario() {
        try {
            // You'll need to add this method to your UsuarioDAO
            // For now, we'll use a simple query approach
            String sql = "SELECT presupuesto_actual FROM usuarios WHERE id = ?";

            try (java.sql.Connection conn = finanzas.dao.ConexionDB.getConnection();
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