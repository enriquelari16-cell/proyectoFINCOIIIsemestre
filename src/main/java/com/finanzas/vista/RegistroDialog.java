package com.finanzas.vista;

import com.finanzas.controlador.FinanzasController;
import com.finanzas.modelo.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Diálogo mejorado para registro de nuevos usuarios con validaciones
 * y consejos educativos integrados.
 */
public class RegistroDialog extends JDialog {

    private FinanzasController controlador;
    private JFrame parent;

    // Campos del formulario
    private JTextField txtNombre;
    private JPasswordField txtContrasena;
    private JPasswordField txtConfirmarContrasena;
    private JTextField txtEdad;
    private JComboBox<String> comboTipoUso;
    private JTextField txtPresupuestoInicial;

    // Botones
    private JButton btnRegistrar;
    private JButton btnCancelar;

    public RegistroDialog(JFrame parent, FinanzasController controlador) {
        super(parent, "Registro de Nuevo Usuario", true);
        this.parent = parent;
        this.controlador = controlador;

        initComponents();
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    public RegistroDialog(LoginFrame parent, FinanzasController controladorRegistro) {
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel principal
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = new JLabel("Crear Nueva Cuenta", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelPrincipal.add(lblTitulo, gbc);

        // Nombre
        gbc.gridwidth = 1; gbc.gridy = 1;
        gbc.gridx = 0;
        panelPrincipal.add(new JLabel("Nombre de usuario:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        panelPrincipal.add(txtNombre, gbc);

        // Contraseña
        gbc.gridy = 2; gbc.gridx = 0;
        panelPrincipal.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        txtContrasena = new JPasswordField(15);
        panelPrincipal.add(txtContrasena, gbc);

        // Confirmar contraseña
        gbc.gridy = 3; gbc.gridx = 0;
        panelPrincipal.add(new JLabel("Confirmar contraseña:"), gbc);
        gbc.gridx = 1;
        txtConfirmarContrasena = new JPasswordField(15);
        panelPrincipal.add(txtConfirmarContrasena, gbc);

        // Edad
        gbc.gridy = 4; gbc.gridx = 0;
        panelPrincipal.add(new JLabel("Edad:"), gbc);
        gbc.gridx = 1;
        txtEdad = new JTextField(5);
        panelPrincipal.add(txtEdad, gbc);

        // Tipo de uso
        gbc.gridy = 5; gbc.gridx = 0;
        panelPrincipal.add(new JLabel("Tipo de uso:"), gbc);
        gbc.gridx = 1;
        String[] tiposUso = {"Personal", "Familiar", "Empresarial", "Estudiante"};
        comboTipoUso = new JComboBox<>(tiposUso);
        panelPrincipal.add(comboTipoUso, gbc);

        // Presupuesto inicial
        gbc.gridy = 6; gbc.gridx = 0;
        panelPrincipal.add(new JLabel("Presupuesto inicial ($):"), gbc);
        gbc.gridx = 1;
        txtPresupuestoInicial = new JTextField(10);
        panelPrincipal.add(txtPresupuestoInicial, gbc);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnRegistrar = new JButton("Crear Cuenta");
        btnRegistrar.setBackground(new Color(50, 150, 100));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.addActionListener(this::registrarUsuario);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(200, 50, 50));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);

        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 2;
        panelPrincipal.add(panelBotones, gbc);

        add(panelPrincipal, BorderLayout.CENTER);

        // Configurar validación en tiempo real
        configurarValidacionTiempoReal();

        // Configurar teclas
        configurarTeclasEnter();
    }

    private void configurarValidacionTiempoReal() {
        // Validar contraseñas coinciden
        txtConfirmarContrasena.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                validarContrasenas();
            }
        });

        txtContrasena.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                validarContrasenas();
            }
        });
    }

    private void validarContrasenas() {
        String pass1 = new String(txtContrasena.getPassword());
        String pass2 = new String(txtConfirmarContrasena.getPassword());

        if (!pass1.isEmpty() && !pass2.isEmpty()) {
            if (pass1.equals(pass2)) {
                txtConfirmarContrasena.setBackground(Color.WHITE);
            } else {
                txtConfirmarContrasena.setBackground(new Color(255, 200, 200));
            }
        }
    }

    private void configurarTeclasEnter() {
        // Enter en campos pasa al siguiente
        txtNombre.addActionListener(e -> txtContrasena.requestFocus());
        txtContrasena.addActionListener(e -> txtConfirmarContrasena.requestFocus());
        txtConfirmarContrasena.addActionListener(e -> txtEdad.requestFocus());
        txtEdad.addActionListener(e -> txtPresupuestoInicial.requestFocus());
        txtPresupuestoInicial.addActionListener(e -> btnRegistrar.doClick());
    }

    private void registrarUsuario(ActionEvent e) {
        try {
            // Obtener y validar datos
            String nombre = txtNombre.getText().trim();
            String contrasena = new String(txtContrasena.getPassword());
            String confirmarContrasena = new String(txtConfirmarContrasena.getPassword());
            String edadStr = txtEdad.getText().trim();
            String tipoUso = (String) comboTipoUso.getSelectedItem();
            String presupuestoStr = txtPresupuestoInicial.getText().trim();

            // Validaciones
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre de usuario es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }

            if (contrasena.length() < 4) {
                JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 4 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
                txtContrasena.requestFocus();
                return;
            }

            if (!contrasena.equals(confirmarContrasena)) {
                JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
                txtConfirmarContrasena.requestFocus();
                return;
            }

            int edad = Integer.parseInt(edadStr);
            if (edad < 13 || edad > 120) {
                JOptionPane.showMessageDialog(this, "La edad debe estar entre 13 y 120 años", "Error", JOptionPane.ERROR_MESSAGE);
                txtEdad.requestFocus();
                return;
            }

            double presupuestoInicial = Double.parseDouble(presupuestoStr);
            if (presupuestoInicial <= 0) {
                JOptionPane.showMessageDialog(this, "El presupuesto inicial debe ser mayor a cero", "Error", JOptionPane.ERROR_MESSAGE);
                txtPresupuestoInicial.requestFocus();
                return;
            }

            // Crear usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setContrasena(contrasena);
            nuevoUsuario.setEdad((byte) edad);
            nuevoUsuario.setTipoUso(tipoUso);
            nuevoUsuario.setPresupuestoInicial(presupuestoInicial);

            // Registrar usuario
            if (controlador.registrarUsuario(nuevoUsuario)) {
                JOptionPane.showMessageDialog(this,
                        String.format("✅ ¡Cuenta creada exitosamente!\n\n" +
                                "Bienvenido %s\n\n" +
                                "💡 Consejos iniciales:\n" +
                                "• Registra tus primeros ingresos y gastos\n" +
                                "• Establece metas de ahorro realistas\n" +
                                "• Revisa tus finanzas semanalmente\n\n" +
                                "¡Comienza tu viaje hacia la libertad financiera!",
                                nombre),
                        "Registro Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);

                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al crear la cuenta. El nombre de usuario ya existe.",
                        "Error de Registro",
                        JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese valores numéricos válidos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}