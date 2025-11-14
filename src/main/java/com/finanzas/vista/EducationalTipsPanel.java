package com.finanzas.vista;

import com.finanzas.controlador.FinanzasController;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Panel educativo que muestra consejos financieros personalizados
 * basados en el comportamiento del usuario.
 */
public class EducationalTipsPanel extends JPanel {

    private FinanzasController controlador;
    private JTextArea txtConsejos;
    private JButton btnActualizarConsejos;
    private JButton btnMostrarEstadisticas;

    public EducationalTipsPanel(FinanzasController controlador) {
        this.controlador = controlador;
        initComponents();
        cargarConsejos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("💡 Consejos Financieros Educativos"));

        // Panel superior con botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnActualizarConsejos = new JButton("Actualizar Consejos");
        btnActualizarConsejos.setBackground(new Color(100, 150, 200));
        btnActualizarConsejos.setForeground(Color.WHITE);
        btnActualizarConsejos.addActionListener(e -> cargarConsejos());

        btnMostrarEstadisticas = new JButton("Ver Estadísticas");
        btnMostrarEstadisticas.setBackground(new Color(150, 100, 200));
        btnMostrarEstadisticas.setForeground(Color.WHITE);
        btnMostrarEstadisticas.addActionListener(e -> mostrarEstadisticas());

        panelBotones.add(btnActualizarConsejos);
        panelBotones.add(btnMostrarEstadisticas);

        // Área de texto para consejos
        txtConsejos = new JTextArea();
        txtConsejos.setEditable(false);
        txtConsejos.setFont(new Font("Arial", Font.PLAIN, 14));
        txtConsejos.setWrapStyleWord(true);
        txtConsejos.setLineWrap(true);
        txtConsejos.setOpaque(false);
        txtConsejos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(txtConsejos);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(panelBotones, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void cargarConsejos() {
        List<String> consejos = controlador.obtenerConsejosEducativos();

        if (consejos.isEmpty()) {
            txtConsejos.setText("¡Excelente trabajo! Sigue así.\n\n" +
                    "💡 Recuerda:\n" +
                    "• Registra todas tus transacciones\n" +
                    "• Establece metas realistas\n" +
                    "• Revisa tus gastos regularmente\n" +
                    "• Ahorra al menos el 20% de tus ingresos");
        } else {
            StringBuilder textoConsejos = new StringBuilder();
            textoConsejos.append("CONSEJOS PERSONALIZADOS PARA TI:\n\n");

            for (int i = 0; i < consejos.size(); i++) {
                textoConsejos.append(String.format("%d. %s\n\n", i + 1, consejos.get(i)));
            }

            textoConsejos.append("Recuerda que la educación financiera es un proceso continuo. " +
                    "¡Cada paso cuenta para construir un futuro financiero sólido!");

            txtConsejos.setText(textoConsejos.toString());
        }

        // Cambiar color del texto según la cantidad de consejos
        if (consejos.size() > 3) {
            txtConsejos.setForeground(new Color(200, 50, 50)); // Rojo para muchos consejos
        } else if (consejos.size() > 1) {
            txtConsejos.setForeground(new Color(200, 150, 50)); // Naranja para algunos consejos
        } else {
            txtConsejos.setForeground(new Color(50, 150, 50)); // Verde para pocos consejos
        }
    }

    private void mostrarEstadisticas() {
        Map<String, Object> estadisticas = controlador.obtenerEstadisticasGenerales();

        String mensaje = String.format(
            "📊 TUS ESTADÍSTICAS FINANCIERAS:\n\n" +
            "Saldo Actual: $%.2f\n" +
            "Presupuesto Inicial: $%.2f\n" +
            "Total Transacciones: %d\n" +
            "Total Ingresos: $%.2f\n" +
            "Total Gastos: $%.2f\n" +
            "Balance General: $%.2f\n\n" +
            "Metas: %d total, %d completadas\n" +
            "Progreso Promedio: %.1f%%\n\n" +
            "¡Sigue trabajando en tus finanzas!",
            estadisticas.get("presupuestoActual"),
            estadisticas.get("presupuestoInicial"),
            estadisticas.get("totalTransacciones"),
            estadisticas.get("totalIngresos"),
            estadisticas.get("totalGastos"),
            estadisticas.get("balanceGeneral"),
            estadisticas.get("totalMetas"),
            estadisticas.get("metasCompletadas"),
            estadisticas.get("progresoPromedio")
        );

        JOptionPane.showMessageDialog(this, mensaje, "Estadísticas Financieras",
                JOptionPane.INFORMATION_MESSAGE);
    }
}