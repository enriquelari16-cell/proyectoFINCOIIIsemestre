package finanzas.vista;

import finanzas.dao.UsuarioDAO;
import finanzas.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistroDialog extends JDialog {
    private JTextField txtNombre;
    private JTextField txtEdad;
    private JComboBox<String> cmbTipoUso;
    private JPasswordField txtContrasena;
    private JPasswordField txtConfirmarContrasena;
    private JTextField txtPresupuestoInicial;
    private UsuarioDAO usuarioDAO;

    public RegistroDialog(JFrame parent, UsuarioDAO usuarioDAO) {
        super(parent, "Crear Nueva Cuenta", true);
        this.usuarioDAO = usuarioDAO;
        initComponents();
    }

    private void initComponents() {
        setSize(450, 450);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = new JLabel("Crear Nueva Cuenta", JLabel.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        // Campo Nombre de Usuario
        JLabel lblNombre = new JLabel("Nombre de Usuario:");
        txtNombre = new JTextField(20);
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblNombre, gbc);
        gbc.gridx = 1; panel.add(txtNombre, gbc);

        // Campo Edad
        JLabel lblEdad = new JLabel("Edad:");
        txtEdad = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblEdad, gbc);
        gbc.gridx = 1; panel.add(txtEdad, gbc);

        // Campo Tipo de Uso
        JLabel lblTipoUso = new JLabel("Tipo de Uso:");
        String[] tiposUso = {"Personal", "Familiar", "Empresarial", "Estudiante"};
        cmbTipoUso = new JComboBox<>(tiposUso);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(lblTipoUso, gbc);
        gbc.gridx = 1; panel.add(cmbTipoUso, gbc);

        // Campo Contraseña
        JLabel lblContrasena = new JLabel("Contraseña:");
        txtContrasena = new JPasswordField(20);
        gbc.gridx = 0; gbc.gridy = 4; panel.add(lblContrasena, gbc);
        gbc.gridx = 1; panel.add(txtContrasena, gbc);

        // Campo Confirmar Contraseña
        JLabel lblConfirmar = new JLabel("Confirmar Contraseña:");
        txtConfirmarContrasena = new JPasswordField(20);
        gbc.gridx = 0; gbc.gridy = 5; panel.add(lblConfirmar, gbc);
        gbc.gridx = 1; panel.add(txtConfirmarContrasena, gbc);

        // Campo Presupuesto Inicial
        JLabel lblPresupuesto = new JLabel("Presupuesto Inicial:");
        txtPresupuestoInicial = new JTextField("0.00", 20);
        gbc.gridx = 0; gbc.gridy = 6; panel.add(lblPresupuesto, gbc);
        gbc.gridx = 1; panel.add(txtPresupuestoInicial, gbc);

        // Nota informativa
        JLabel lblNota = new JLabel("<html><i>El presupuesto inicial es opcional (puedes dejarlo en 0)</i></html>");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        panel.add(lblNota, gbc);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBackground(Color.WHITE);

        JButton btnRegistrar = new JButton("Crear Cuenta");
        btnRegistrar.setBackground(new Color(100, 100, 200));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setPreferredSize(new Dimension(120, 35));
        btnRegistrar.addActionListener(this::registrarUsuario);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(150, 150, 150));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        panel.add(panelBotones, gbc);

        add(panel);

        // Focus en el primer campo
        SwingUtilities.invokeLater(() -> txtNombre.requestFocus());
    }

    private void registrarUsuario(ActionEvent e) {
        // Obtener datos de los campos
        String nombre = txtNombre.getText().trim();
        String edadTexto = txtEdad.getText().trim();
        String tipoUso = (String) cmbTipoUso.getSelectedItem();
        String contrasena = new String(txtContrasena.getPassword());
        String confirmarContrasena = new String(txtConfirmarContrasena.getPassword());
        String presupuestoTexto = txtPresupuestoInicial.getText().trim();

        // Validaciones
        if (!validarCampos(nombre, edadTexto, contrasena, confirmarContrasena, presupuestoTexto)) {
            return;
        }

        // Verificar si el usuario ya existe
        if (usuarioDAO.existeUsuario(nombre)) {
            JOptionPane.showMessageDialog(this,
                    "El nombre de usuario ya existe. Elija otro nombre.",
                    "Usuario Existente",
                    JOptionPane.WARNING_MESSAGE);
            txtNombre.selectAll();
            txtNombre.requestFocus();
            return;
        }

        try {
            byte edad = Byte.parseByte(edadTexto);
            double presupuestoInicial = Double.parseDouble(presupuestoTexto);

            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario(nombre, edad, tipoUso, contrasena, presupuestoInicial);

            // Intentar registrar el usuario
            if (usuarioDAO.registrarUsuario(nuevoUsuario)) {
                JOptionPane.showMessageDialog(this,
                        "¡Cuenta creada exitosamente!\nYa puede iniciar sesión.",
                        "Registro Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al crear la cuenta. Intente nuevamente.",
                        "Error de Registro",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Verifique que la edad y el presupuesto sean números válidos.",
                    "Datos Inválidos",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos(String nombre, String edadTexto, String contrasena,
                                  String confirmarContrasena, String presupuestoTexto) {

        // Validar campos vacíos
        if (nombre.isEmpty()) {
            mostrarError("El nombre de usuario es obligatorio.", txtNombre);
            return false;
        }

        if (edadTexto.isEmpty()) {
            mostrarError("La edad es obligatoria.", txtEdad);
            return false;
        }

        if (contrasena.isEmpty()) {
            mostrarError("La contraseña es obligatoria.", txtContrasena);
            return false;
        }

        if (confirmarContrasena.isEmpty()) {
            mostrarError("Debe confirmar la contraseña.", txtConfirmarContrasena);
            return false;
        }

        // Validar longitud del nombre de usuario
        if (nombre.length() < 3) {
            mostrarError("El nombre de usuario debe tener al menos 3 caracteres.", txtNombre);
            return false;
        }

        // Validar edad
        try {
            byte edad = Byte.parseByte(edadTexto);
            if (edad < 1 || edad > 120) {
                mostrarError("La edad debe estar entre 1 y 120 años.", txtEdad);
                return false;
            }
        } catch (NumberFormatException ex) {
            mostrarError("La edad debe ser un número válido.", txtEdad);
            return false;
        }

        // Validar longitud de contraseña
        if (contrasena.length() < 4) {
            mostrarError("La contraseña debe tener al menos 4 caracteres.", txtContrasena);
            return false;
        }

        // Validar que las contraseñas coincidan
        if (!contrasena.equals(confirmarContrasena)) {
            mostrarError("Las contraseñas no coinciden.", txtConfirmarContrasena);
            return false;
        }

        // Validar presupuesto inicial
        if (presupuestoTexto.isEmpty()) {
            txtPresupuestoInicial.setText("0.00");
        } else {
            try {
                double presupuesto = Double.parseDouble(presupuestoTexto);
                if (presupuesto < 0) {
                    mostrarError("El presupuesto inicial no puede ser negativo.", txtPresupuestoInicial);
                    return false;
                }
            } catch (NumberFormatException ex) {
                mostrarError("El presupuesto inicial debe ser un número válido.", txtPresupuestoInicial);
                return false;
            }
        }

        return true;
    }

    private void mostrarError(String mensaje, JComponent componente) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Validación", JOptionPane.ERROR_MESSAGE);
        componente.requestFocus();
        if (componente instanceof JTextField) {
            ((JTextField) componente).selectAll();
        }
    }
}