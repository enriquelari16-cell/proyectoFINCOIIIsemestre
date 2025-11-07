package finanzas.vista;

import finanzas.modelo.Usuario;
import finanzas.dao.TransaccionDAO;
import finanzas.dao.MetaDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;

public class ReportesFinancierosDialog extends JDialog {
    private final Usuario usuario;
    private final TransaccionDAO transaccionDAO;
    private final MetaDAO metaDAO;
    private final JTextArea areaReporte;
    private final JComboBox<String> comboTipoReporte;
    private final JComboBox<String> comboPeriodo;

    public ReportesFinancierosDialog(JFrame parent, Usuario usuario) {
        super(parent, "Reportes Financieros", true);
        this.usuario = usuario;
        this.transaccionDAO = new TransaccionDAO();
        this.metaDAO = new MetaDAO();

        setSize(700, 600);
        setLocationRelativeTo(parent);

        // Inicializar componentes
        this.areaReporte = new JTextArea();
        areaReporte.setEditable(false);
        areaReporte.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaReporte.setBackground(Color.WHITE);

        this.comboTipoReporte = new JComboBox<>(new String[]{
                "Reporte General",
                "Resumen Mensual",
                "Análisis de Gastos",
                "Progreso de Metas",
                "Comparativo Ingresos vs Gastos"
        });

        this.comboPeriodo = new JComboBox<>(new String[]{
                "Último Mes",
                "Últimos 3 Meses",
                "Últimos 6 Meses",
                "Último Año",
                "Todo el Tiempo"
        });

        initUI();
        generarReporteGeneral();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Panel superior - Controles
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelControles.setBorder(BorderFactory.createTitledBorder("Opciones de Reporte"));

        panelControles.add(new JLabel("Tipo de Reporte:"));
        panelControles.add(comboTipoReporte);
        comboTipoReporte.addActionListener(this::cambiarTipoReporte);

        panelControles.add(new JLabel("Período:"));
        panelControles.add(comboPeriodo);
        comboPeriodo.addActionListener(this::cambiarTipoReporte);

        JButton btnGenerar = new JButton("Generar Reporte");
        btnGenerar.addActionListener(this::generarReporte);
        panelControles.add(btnGenerar);

        add(panelControles, BorderLayout.NORTH);

        // Panel central - Área de reporte
        JScrollPane scrollPane = new JScrollPane(areaReporte);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior - Botones
        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnExportar = new JButton("Exportar Reporte");
        btnExportar.addActionListener(this::exportarReporte);

        JButton btnImprimir = new JButton("Imprimir");
        btnImprimir.addActionListener(this::imprimirReporte);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        panelBotones.add(btnExportar);
        panelBotones.add(btnImprimir);
        panelBotones.add(btnCerrar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cambiarTipoReporte(ActionEvent e) {
        generarReporte(e);
    }

    private void generarReporte(ActionEvent e) {
        String tipoReporte = (String) comboTipoReporte.getSelectedItem();
        String periodo = (String) comboPeriodo.getSelectedItem();

        switch (tipoReporte) {
            case "Reporte General":
                generarReporteGeneral();
                break;
            case "Resumen Mensual":
                generarResumenMensual(periodo);
                break;
            case "Análisis de Gastos":
                generarAnalisisGastos(periodo);
                break;
            case "Progreso de Metas":
                generarProgresoMetas();
                break;
            case "Comparativo Ingresos vs Gastos":
                generarComparativoIngresosGastos(periodo);
                break;
        }
    }

    private LocalDate obtenerFechaInicioPeriodo(String periodo) {
        LocalDate ahora = LocalDate.now();
        switch (periodo) {
            case "Último Mes":
                return ahora.minusMonths(1);
            case "Últimos 3 Meses":
                return ahora.minusMonths(3);
            case "Últimos 6 Meses":
                return ahora.minusMonths(6);
            case "Último Año":
                return ahora.minusYears(1);
            case "Todo el Tiempo":
            default:
                return LocalDate.of(2000, 1, 1);
        }
    }

    private void generarReporteGeneral() {
        StringBuilder reporte = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        reporte.append("═══════════════════════════════════════════════════════════════\n");
        reporte.append("                    REPORTE FINANCIERO GENERAL\n");
        reporte.append("═══════════════════════════════════════════════════════════════\n\n");

        reporte.append("Usuario: ").append(usuario.getNombre()).append("\n");
        reporte.append("Fecha del Reporte: ").append(LocalDate.now().format(formatter)).append("\n\n");

        // Información del presupuesto
        reporte.append("INFORMACIÓN DEL PRESUPUESTO:\n");
        reporte.append("───────────────────────────────────────────────────────────────\n");
        reporte.append(String.format("Presupuesto Inicial: $%,.2f\n", usuario.getPresupuestoInicial()));
        reporte.append(String.format("Presupuesto Actual:  $%,.2f\n", usuario.getPresupuestoActual()));

        double diferencia = usuario.getPresupuestoActual() - usuario.getPresupuestoInicial();
        String estado = diferencia >= 0 ? "GANANCIA" : "PÉRDIDA";
        reporte.append(String.format("Diferencia:          $%,.2f (%s)\n\n", Math.abs(diferencia), estado));

        // Estadísticas de transacciones
        Map<String, Object> estadisticas = transaccionDAO.obtenerEstadisticasGenerales(usuario.getId());

        reporte.append("ESTADÍSTICAS DE TRANSACCIONES:\n");
        reporte.append("───────────────────────────────────────────────────────────────\n");
        reporte.append(String.format("Total de Transacciones: %,d\n", (Integer) estadisticas.getOrDefault("totalTransacciones", 0)));
        reporte.append(String.format("Total Ingresos:         $%,.2f\n", (Double) estadisticas.getOrDefault("totalIngresos", 0.0)));
        reporte.append(String.format("Total Gastos:           $%,.2f\n", (Double) estadisticas.getOrDefault("totalGastos", 0.0)));
        reporte.append(String.format("Promedio por Ingreso:   $%,.2f\n", (Double) estadisticas.getOrDefault("promedioIngresos", 0.0)));
        reporte.append(String.format("Promedio por Gasto:     $%,.2f\n\n", (Double) estadisticas.getOrDefault("promedioGastos", 0.0)));

        // Análisis de metas
        Map<String, Object> estatisticasMetas = metaDAO.obtenerEstadisticasMetas(usuario.getId());

        reporte.append("ANÁLISIS DE METAS:\n");
        reporte.append("───────────────────────────────────────────────────────────────\n");
        reporte.append(String.format("Total de Metas:         %,d\n", (Integer) estatisticasMetas.getOrDefault("totalMetas", 0)));
        reporte.append(String.format("Metas Completadas:      %,d\n", (Integer) estatisticasMetas.getOrDefault("metasCompletadas", 0)));
        reporte.append(String.format("Progreso Promedio:      %,.1f%%\n", (Double) estatisticasMetas.getOrDefault("progresoPromedio", 0.0)));
        reporte.append(String.format("Total Ahorrado:         $%,.2f\n\n", (Double) estatisticasMetas.getOrDefault("totalAhorrado", 0.0)));

        // Recomendaciones
        reporte.append("RECOMENDACIONES:\n");
        reporte.append("───────────────────────────────────────────────────────────────\n");

        if (diferencia < 0) {
            reporte.append("• Sus gastos han superado sus ingresos iniciales.\n");
            reporte.append("• Considere revisar sus gastos y crear un plan de ahorro.\n");
        } else {
            reporte.append("• ¡Excelente! Ha logrado mantener un balance positivo.\n");
            reporte.append("• Considere establecer nuevas metas de ahorro.\n");
        }

        double gastoPromedio = (Double) estadisticas.getOrDefault("promedioGastos", 0.0);
        if (gastoPromedio > usuario.getPresupuestoActual() * 0.1) {
            reporte.append("• Sus gastos promedio son altos, considere un presupuesto más detallado.\n");
        }

        areaReporte.setText(reporte.toString());
    }

    private void generarResumenMensual(String periodo) {
        StringBuilder reporte = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        reporte.append("═══════════════════════════════════════════════════════════════\n");
        reporte.append("                    RESUMEN MENSUAL\n");
        reporte.append("═══════════════════════════════════════════════════════════════\n\n");

        reporte.append("Usuario: ").append(usuario.getNombre()).append("\n");
        reporte.append("Período: ").append(periodo).append("\n");
        reporte.append("Fecha del Reporte: ").append(LocalDate.now().format(formatter)).append("\n\n");

        LocalDate fechaInicio = obtenerFechaInicioPeriodo(periodo);
        LocalDate fechaFin = LocalDate.now();

        if (fechaInicio.isAfter(fechaFin)) {
            areaReporte.setText("Error: Fecha de inicio posterior a fecha final");
            return;
        }

        int año = fechaFin.getYear();
        List<Map<String, Object>> datosMensuales = transaccionDAO.obtenerDatosMensuales(usuario.getId(), año);

        reporte.append("RESUMEN POR MES:\n");
        reporte.append("───────────────────────────────────────────────────────────────\n");
        reporte.append(String.format("%-12s %12s %12s %12s\n", "Mes", "Ingresos", "Gastos", "Balance"));
        reporte.append("───────────────────────────────────────────────────────────────\n");

        double totalIngresos = 0, totalGastos = 0;

        for (Map<String, Object> datos : datosMensuales) {
            String mes = (String) datos.get("nombreMes");
            double ingresos = (Double) datos.get("ingresos");
            double gastos = (Double) datos.get("gastos");
            double balance = ingresos - gastos;

            totalIngresos += ingresos;
            totalGastos += gastos;

            reporte.append(String.format("%-12s $%,11.2f $%,11.2f $%,11.2f\n",
                    mes, ingresos, gastos, balance));
        }

        reporte.append("───────────────────────────────────────────────────────────────\n");
        reporte.append(String.format("%-12s $%,11.2f $%,11.2f $%,11.2f\n",
                "TOTAL", totalIngresos, totalGastos, totalIngresos - totalGastos));

        areaReporte.setText(reporte.toString());
    }

    private void generarAnalisisGastos(String periodo) {
        StringBuilder reporte = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        reporte.append("═══════════════════════════════════════════════════════════════\n");
        reporte.append("                    ANÁLISIS DE GASTOS\n");
        reporte.append("═══════════════════════════════════════════════════════════════\n\n");

        reporte.append("Usuario: ").append(usuario.getNombre()).append("\n");
        reporte.append("Período: ").append(periodo).append("\n");
        reporte.append("Fecha del Reporte: ").append(LocalDate.now().format(formatter)).append("\n\n");

        LocalDate fechaInicio = obtenerFechaInicioPeriodo(periodo);
        LocalDate fechaFin = LocalDate.now();

        if (fechaInicio.isAfter(fechaFin)) {
            areaReporte.setText("Error: Fecha de inicio posterior a fecha final");
            return;
        }

        Map<String, Object> analisisGastos = transaccionDAO.obtenerEstadisticasGastos(usuario.getId(), fechaInicio, fechaFin);

        reporte.append("ESTADÍSTICAS DE GASTOS:\n");
        reporte.append("───────────────────────────────────────────────────────────────\n");
        reporte.append(String.format("Total de Gastos:        $%,.2f\n", (Double) analisisGastos.getOrDefault("totalGastos", 0.0)));
        reporte.append(String.format("Número de Gastos:       %,d\n", (Integer) analisisGastos.getOrDefault("numeroGastos", 0)));
        reporte.append(String.format("Gasto Promedio:         $%,.2f\n", (Double) analisisGastos.getOrDefault("gastoPromedio", 0.0)));
        reporte.append(String.format("Gasto Máximo:           $%,.2f\n", (Double) analisisGastos.getOrDefault("gastoMaximo", 0.0)));
        reporte.append(String.format("Gasto Mínimo:           $%,.2f\n\n", (Double) analisisGastos.getOrDefault("gastoMinimo", 0.0)));

        // Recomendaciones
        reporte.append("RECOMENDACIONES:\n");
        reporte.append("───────────────────────────────────────────────────────────────\n");

        double gastoPromedio = (Double) analisisGastos.getOrDefault("gastoPromedio", 0.0);
        double presupuestoActual = usuario.getPresupuestoActual();

        if (gastoPromedio > presupuestoActual * 0.2) {
            reporte.append("• Sus gastos promedio son altos (>20% del presupuesto).\n");
            reporte.append("• Considere establecer límites de gasto más estrictos.\n");
        }

        if ((Integer) analisisGastos.getOrDefault("numeroGastos", 0) > 50) {
            reporte.append("• Tiene muchas transacciones de gasto.\n");
            reporte.append("• Considere consolidar compras pequeñas.\n");
        }

        areaReporte.setText(reporte.toString());
    }

    private void generarProgresoMetas() {
        StringBuilder reporte = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        reporte.append("═══════════════════════════════════════════════════════════════\n");
        reporte.append("                    PROGRESO DE METAS\n");
        reporte.append("═══════════════════════════════════════════════════════════════\n\n");

        reporte.append("Usuario: ").append(usuario.getNombre()).append("\n");
        reporte.append("Fecha del Reporte: ").append(LocalDate.now().format(formatter)).append("\n\n");

        var metas = metaDAO.obtenerMetasPorUsuario(usuario.getId());

        if (metas.isEmpty()) {
            reporte.append("No hay metas registradas.\n");
            reporte.append("Considere establecer metas de ahorro para mejorar su planificación financiera.\n");
        } else {
            reporte.append("DETALLE DE METAS:\n");
            reporte.append("───────────────────────────────────────────────────────────────\n");
            reporte.append(String.format("%-20s %12s %12s %10s %8s\n",
                    "Nombre", "Objetivo", "Ahorrado", "Progreso", "Estado"));
            reporte.append("───────────────────────────────────────────────────────────────\n");

            double totalObjetivo = 0, totalAhorrado = 0;
            int metasCompletadas = 0;

            for (var meta : metas) {
                double progreso = (meta.getAhorroActual() / meta.getMontoObjetivo()) * 100;
                String estado = progreso >= 100 ? "COMPLETA" : "PENDIENTE";

                if (progreso >= 100) metasCompletadas++;
                totalObjetivo += meta.getMontoObjetivo();
                totalAhorrado += meta.getAhorroActual();

                String nombre = meta.getNombre().length() > 20 ?
                        meta.getNombre().substring(0, 17) + "..." : meta.getNombre();

                reporte.append(String.format("%-20s $%,11.2f $%,11.2f %9.1f%% %8s\n",
                        nombre, meta.getMontoObjetivo(), meta.getAhorroActual(),
                        progreso, estado));
            }

            reporte.append("───────────────────────────────────────────────────────────────\n");
            reporte.append(String.format("%-20s $%,11.2f $%,11.2f %9.1f%%\n",
                    "TOTALES", totalObjetivo, totalAhorrado,
                    (totalAhorrado / totalObjetivo) * 100));

            reporte.append("\nRESUMEN:\n");
            reporte.append("───────────────────────────────────────────────────────────────\n");
            reporte.append(String.format("Total de Metas:         %d\n", metas.size()));
            reporte.append(String.format("Metas Completadas:      %d\n", metasCompletadas));
            reporte.append(String.format("Metas Pendientes:       %d\n", metas.size() - metasCompletadas));
            reporte.append(String.format("Progreso Global:        %.1f%%\n", (totalAhorrado / totalObjetivo) * 100));
        }

        areaReporte.setText(reporte.toString());
    }

    private void generarComparativoIngresosGastos(String periodo) {
        StringBuilder reporte = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        reporte.append("═══════════════════════════════════════════════════════════════\n");
        reporte.append("                COMPARATIVO INGRESOS VS GASTOS\n");
        reporte.append("═══════════════════════════════════════════════════════════════\n\n");

        reporte.append("Usuario: ").append(usuario.getNombre()).append("\n");
        reporte.append("Período: ").append(periodo).append("\n");
        reporte.append("Fecha del Reporte: ").append(LocalDate.now().format(formatter)).append("\n\n");

        String tipoComparacion = periodo.contains("Año") ? "ANUAL" :
                periodo.contains("Mes") ? "MENSUAL" : "TRIMESTRAL";
        int año = LocalDate.now().getYear();

        List<Map<String, Object>> comparativo = transaccionDAO.obtenerComparativoIngresosGastos(
                usuario.getId(), tipoComparacion, año);

        if (comparativo.isEmpty()) {
            areaReporte.setText("No hay datos disponibles para el período seleccionado");
            return;
        }

        double totalIngresos = comparativo.stream()
                .mapToDouble(m -> (Double) m.get("ingresos"))
                .sum();
        double totalGastos = comparativo.stream()
                .mapToDouble(m -> (Double) m.get("gastos"))
                .sum();
        double balance = totalIngresos - totalGastos;

        reporte.append("RESUMEN COMPARATIVO:\n");
        reporte.append("───────────────────────────────────────────────────────────────\n");
        reporte.append(String.format("Total Ingresos:         $%,.2f\n", totalIngresos));
        reporte.append(String.format("Total Gastos:           $%,.2f\n", totalGastos));
        reporte.append(String.format("Balance Neto:           $%,.2f\n", balance));

        double ratio = totalIngresos > 0 ? (totalGastos / totalIngresos) * 100 : 0;
        reporte.append(String.format("Ratio Gastos/Ingresos:  %,.1f%%\n\n", ratio));

        // Gráfico ASCII simple
        reporte.append("GRÁFICO COMPARATIVO:\n");
        reporte.append("───────────────────────────────────────────────────────────────\n");

        double maxVal = Math.max(totalIngresos, totalGastos);
        int barrasIngresos = (int) ((totalIngresos / maxVal) * 50);
        int barrasGastos = (int) ((totalGastos / maxVal) * 50);

        reporte.append("Ingresos:  ");
        for (int i = 0; i < barrasIngresos; i++) reporte.append("█");
        reporte.append(String.format(" $%,.2f\n", totalIngresos));

        reporte.append("Gastos:    ");
        for (int i = 0; i < barrasGastos; i++) reporte.append("█");
        reporte.append(String.format(" $%,.2f\n\n", totalGastos));

        // Análisis y recomendaciones
        reporte.append("ANÁLISIS:\n");
        reporte.append("───────────────────────────────────────────────────────────────\n");

        if (balance > 0) {
            reporte.append("✓ Balance POSITIVO - Sus ingresos superan sus gastos.\n");
            reporte.append("✓ Está en una buena posición financiera.\n");
        } else {
            reporte.append("⚠ Balance NEGATIVO - Sus gastos superan sus ingresos.\n");
            reporte.append("⚠ Necesita revisar y reducir sus gastos.\n");
        }

        if (ratio > 80) {
            reporte.append("⚠ Alto porcentaje de gastos (>80% de ingresos).\n");
        } else if (ratio > 60) {
            reporte.append("• Porcentaje moderado de gastos (60-80% de ingresos).\n");
        } else {
            reporte.append("✓ Buen control de gastos (<60% de ingresos).\n");
        }

        areaReporte.setText(reporte.toString());
    }

    private void exportarReporte(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        String tipoReporte = ((String) comboTipoReporte.getSelectedItem()).replace(" ", "_");
        fileChooser.setSelectedFile(new java.io.File("reporte_" + tipoReporte.toLowerCase() + ".txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile())) {
                writer.write(areaReporte.getText());
                JOptionPane.showMessageDialog(this, "Reporte exportado exitosamente");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void imprimirReporte(ActionEvent e) {
        try {
            areaReporte.print();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al imprimir: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}