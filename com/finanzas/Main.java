package finanzas;

import finanzas.vista.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase principal que inicia la aplicación financiera educativa.
 * Implementa una interfaz moderna con consejos financieros integrados.
 */
public class Main {
    public static void main(String[] args) {
        // Configurar Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo configurar el Look and Feel: " + e.getMessage());
        }

        // Iniciar aplicación en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Mostrar pantalla de bienvenida
                mostrarMensajeBienvenida();

                // Iniciar login
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                System.err.println("Error al iniciar la aplicación: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    /**
     * Muestra un mensaje de bienvenida educativo
     */
    private static void mostrarMensajeBienvenida() {
        System.out.println("==========================================");
        System.out.println("   💰 GESTOR FINANCIERO EDUCATIVO 💰");
        System.out.println("==========================================");
        System.out.println("¡Bienvenido a tu asistente financiero personal!");
        System.out.println();
        System.out.println("Características principales:");
        System.out.println("• Registra ingresos y gastos");
        System.out.println("• Establece metas de ahorro");
        System.out.println("• Recibe consejos financieros personalizados");
        System.out.println("• Visualiza estadísticas detalladas");
        System.out.println("• Interfaz intuitiva y educativa");
        System.out.println();
        System.out.println("¡Comienza tu viaje hacia la libertad financiera!");
        System.out.println("==========================================");
    }
}