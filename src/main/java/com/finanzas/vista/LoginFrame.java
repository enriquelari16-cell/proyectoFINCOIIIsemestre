package com.finanzas.vista;

import com.finanzas.dao.UsuarioDAO;
import com.finanzas.modelo.Usuario;
import com.finanzas.controlador.FinanzasController;


import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtNombre;
    private JPasswordField txtContrasena;
    private JButton btnLogin;
    private UsuarioDAO usuarioDAO;

    public LoginFrame() {
        usuarioDAO = new UsuarioDAO();
        // Configurar Look and Feel moderno
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Usar Look and Feel por defecto si hay error
            ex.printStackTrace();
        }
        initComponents();
    }

    private void initComponents() {
        setTitle("Gestor de Finanzas - Login");
        setSize(420, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Gestor de Finanzas Personales", JLabel.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        JLabel lblUsuario = new JLabel("Usuario:");
        txtNombre = new JTextField(15);
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblUsuario, gbc);
        gbc.gridx = 1; panel.add(txtNombre, gbc);

        JLabel lblContrasena = new JLabel("Contraseña:");
        txtContrasena = new JPasswordField(15);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblContrasena, gbc);
        gbc.gridx = 1; panel.add(txtContrasena, gbc);

        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBackground(new Color(50, 150, 100));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setPreferredSize(new Dimension(130, 35));
        btnLogin.addActionListener(e -> iniciarSesion());

        // Guardar referencia para deshabilitar durante login
        this.btnLogin = btnLogin;

        JButton btnRegistro = new JButton("Crear Cuenta");
        btnRegistro.setBackground(new Color(100, 100, 200));
        btnRegistro.setForeground(Color.BLACK);
        btnRegistro.setPreferredSize(new Dimension(130, 35));
        btnRegistro.addActionListener(e -> mostrarRegistro());

        JPanel botones = new JPanel(new FlowLayout());
        botones.add(btnLogin);
        botones.add(btnRegistro);
        botones.setBackground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(botones, gbc);

        add(panel);

        // Configurar teclas Enter
        configurarTeclasEnter();
    }

    private void configurarTeclasEnter() {
        // Enter en campo usuario pasa a contraseña
        txtNombre.addActionListener(e -> txtContrasena.requestFocus());

        // Enter en campo contraseña inicia sesión
        txtContrasena.addActionListener(e -> iniciarSesion());
    }

    private void iniciarSesion() {
        String nombre = txtNombre.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        if (nombre.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mostrar indicador de carga
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnLogin.setEnabled(false);

        try {
            // Usar el controlador para autenticación
            FinanzasController controlador = new FinanzasController();

            if (controlador.autenticarUsuario(nombre, contrasena)) {
                // Login exitoso
                SwingUtilities.invokeLater(() -> {
                    new MainFrame(controlador.getUsuarioActual()).setVisible(true);
                    dispose();
                });
            } else {
                JOptionPane.showMessageDialog(this,
                        "Credenciales incorrectas. Verifique su usuario y contraseña.",
                        "Error de Autenticación",
                        JOptionPane.ERROR_MESSAGE);
                txtContrasena.setText("");
                txtContrasena.requestFocus();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al conectar con la base de datos: " + e.getMessage(),
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            setCursor(Cursor.getDefaultCursor());
            btnLogin.setEnabled(true);
        }
    }

    private void mostrarRegistro() {
        try {
            // Crear controlador para el registro
            FinanzasController controladorRegistro = new FinanzasController();
            RegistroDialog registro = new RegistroDialog(this, controladorRegistro);
            registro.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al abrir el formulario de registro: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error al iniciar la aplicación: " + e.getMessage(),
                        "Error Fatal",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}