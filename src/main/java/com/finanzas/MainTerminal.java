package com.finanzas;

import com.finanzas.controlador.FinanzasController;
import com.finanzas.modelo.Usuario;
import com.finanzas.modelo.Transaccion;
import com.finanzas.modelo.Meta;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Versión de terminal del gestor financiero educativo.
 * Proporciona una interfaz de línea de comandos completa con todas las funcionalidades.
 */
public class MainTerminal {

    private static FinanzasController controlador;
    private static Scanner scanner;
    private static Usuario usuarioActual;

    public static void main(String[] args) {
        controlador = new FinanzasController();
        scanner = new Scanner(System.in);

        mostrarBienvenida();

        if (iniciarSesion()) {
            mostrarMenuPrincipal();
        }

        scanner.close();
        System.out.println("\n¡Gracias por usar el Gestor Financiero Educativo!");
    }

    private static void mostrarBienvenida() {
        System.out.println("==========================================");
        System.out.println("   💰 GESTOR FINANCIERO EDUCATIVO 💰");
        System.out.println("==========================================");
        System.out.println("¡Bienvenido a tu asistente financiero personal!");
        System.out.println("Versión: Terminal Interactiva");
        System.out.println("==========================================");
    }

    private static boolean iniciarSesion() {
        System.out.println("\n=== INICIO DE SESIÓN ===");

        while (true) {
            System.out.print("Usuario: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Contraseña: ");
            String contrasena = scanner.nextLine().trim();

            if (controlador.autenticarUsuario(nombre, contrasena)) {
                usuarioActual = controlador.getUsuarioActual();
                System.out.println("\n✅ ¡Bienvenido " + usuarioActual.getNombre() + "!");
                mostrarConsejosIniciales();
                return true;
            } else {
                System.out.println("❌ Credenciales incorrectas.");
                System.out.print("¿Desea intentar de nuevo? (s/n): ");
                if (!scanner.nextLine().toLowerCase().startsWith("s")) {
                    return false;
                }
            }
        }
    }

    private static void mostrarConsejosIniciales() {
        System.out.println("\n💡 CONSEJOS PARA EMPEZAR:");
        System.out.println("• Registra tus ingresos y gastos regularmente");
        System.out.println("• Establece metas de ahorro realistas");
        System.out.println("• Revisa tus estadísticas semanalmente");
        System.out.println("• Usa la opción de consejos para aprender más");
        System.out.println();
    }

    private static void mostrarMenuPrincipal() {
        while (true) {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. 📊 Ver Saldo Actual");
            System.out.println("2. 💰 Nueva Transacción");
            System.out.println("3. 📋 Ver Transacciones");
            System.out.println("4. 🎯 Gestionar Metas");
            System.out.println("5. 📈 Ver Estadísticas");
            System.out.println("6. 💡 Consejos Financieros");
            System.out.println("7. 👤 Ver Perfil");
            System.out.println("0. 🚪 Salir");
            System.out.print("\nSeleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine().trim());

                switch (opcion) {
                    case 1 -> mostrarSaldoActual();
                    case 2 -> nuevaTransaccion();
                    case 3 -> verTransacciones();
                    case 4 -> gestionarMetas();
                    case 5 -> verEstadisticas();
                    case 6 -> mostrarConsejos();
                    case 7 -> verPerfil();
                    case 0 -> {
                        controlador.cerrarSesion();
                        return;
                    }
                    default -> System.out.println("❌ Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor ingrese un número válido.");
            }
        }
    }

    private static void mostrarSaldoActual() {
        System.out.println("\n=== SALDO ACTUAL ===");
        System.out.printf("Saldo Actual: $%.2f%n", usuarioActual.getPresupuestoActual());
        System.out.printf("Presupuesto Inicial: $%.2f%n", usuarioActual.getPresupuestoInicial());

        double diferencia = usuarioActual.getPresupuestoActual() - usuarioActual.getPresupuestoInicial();
        if (diferencia > 0) {
            System.out.printf("✅ Has ahorrado: $%.2f%n", diferencia);
        } else if (diferencia < 0) {
            System.out.printf("⚠️ Has gastado por encima: $%.2f%n", Math.abs(diferencia));
        } else {
            System.out.println("📊 Tu saldo está igual al presupuesto inicial.");
        }
    }

    private static void nuevaTransaccion() {
        System.out.println("\n=== NUEVA TRANSACCIÓN ===");

        try {
            System.out.println("Tipo de transacción:");
            System.out.println("1. 💰 Ingreso");
            System.out.println("2. 💸 Gasto");
            System.out.print("Seleccione (1-2): ");

            int tipoOpcion = Integer.parseInt(scanner.nextLine().trim());
            String tipo = (tipoOpcion == 1) ? "Ingreso" : "Gasto";

            System.out.print("Monto: $");
            double monto = Double.parseDouble(scanner.nextLine().trim());

            if (monto <= 0) {
                System.out.println("❌ El monto debe ser mayor a cero.");
                return;
            }

            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine().trim();

            if (descripcion.isEmpty()) {
                System.out.println("❌ La descripción no puede estar vacía.");
                return;
            }

            // Crear transacción
            Transaccion transaccion = new Transaccion();
            transaccion.setUsuarioId(usuarioActual.getId());
            transaccion.setTipo(tipo);
            transaccion.setMonto(monto);
            transaccion.setDescripcion(descripcion);

            // Verificar fondos para gastos
            if (tipo.equals("Gasto") && monto > usuarioActual.getPresupuestoActual()) {
                System.out.printf("⚠️ Esta transacción dejará tu saldo en negativo ($%.2f)%n",
                        usuarioActual.getPresupuestoActual() - monto);
                System.out.print("¿Continuar? (s/n): ");
                if (!scanner.nextLine().toLowerCase().startsWith("s")) {
                    return;
                }
            }

            if (controlador.crearTransaccion(transaccion)) {
                System.out.println("✅ Transacción registrada exitosamente!");
                mostrarSaldoActual();

                // Mostrar consejo si es un gasto alto
                if (tipo.equals("Gasto") && monto > usuarioActual.getPresupuestoActual() * 0.1) {
                    System.out.println("\n💡 Consejo: Este gasto representa más del 10% de tu saldo actual.");
                    System.out.println("   Considera si es una compra necesaria.");
                }
            } else {
                System.out.println("❌ Error al registrar la transacción.");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Por favor ingrese valores numéricos válidos.");
        }
    }

    private static void verTransacciones() {
        System.out.println("\n=== TRANSACCIONES ===");

        List<Transaccion> transacciones = controlador.obtenerTransaccionesFiltradas("Todos", 0);

        if (transacciones.isEmpty()) {
            System.out.println("📝 No tienes transacciones registradas.");
            System.out.println("💡 Registra tu primera transacción para comenzar a rastrear tus finanzas.");
            return;
        }

        System.out.println("Mostrando últimas 10 transacciones:");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.printf("%-3s %-10s %-10s %-30s %-12s%n", "N°", "Tipo", "Monto", "Descripción", "Fecha");
        System.out.println("─────────────────────────────────────────────────────────────");

        int count = 0;
        for (Transaccion t : transacciones) {
            if (count >= 10) break;
            System.out.printf("%-3d %-10s $%-9.2f %-30s %-12s%n",
                    count + 1,
                    t.getTipo(),
                    t.getMonto(),
                    t.getDescripcion().length() > 28 ? t.getDescripcion().substring(0, 25) + "..." : t.getDescripcion(),
                    t.getFecha().toString());
            count++;
        }

        if (transacciones.size() > 10) {
            System.out.println("... y " + (transacciones.size() - 10) + " transacciones más.");
        }
    }

    private static void gestionarMetas() {
        while (true) {
            System.out.println("\n=== GESTIÓN DE METAS ===");
            System.out.println("1. 🎯 Ver Mis Metas");
            System.out.println("2. ➕ Crear Nueva Meta");
            System.out.println("3. 📈 Actualizar Ahorro");
            System.out.println("4. 🗑️ Eliminar Meta");
            System.out.println("0. 🔙 Volver al Menú Principal");
            System.out.print("\nSeleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine().trim());

                switch (opcion) {
                    case 1 -> verMetas();
                    case 2 -> crearMeta();
                    case 3 -> actualizarAhorroMeta();
                    case 4 -> eliminarMeta();
                    case 0 -> { return; }
                    default -> System.out.println("❌ Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor ingrese un número válido.");
            }
        }
    }

    private static void verMetas() {
        List<Meta> metas = controlador.obtenerMetasUsuario();

        if (metas.isEmpty()) {
            System.out.println("\n📝 No tienes metas registradas.");
            System.out.println("💡 Las metas te ayudan a alcanzar tus objetivos financieros.");
            return;
        }

        System.out.println("\n=== MIS METAS ===");
        for (int i = 0; i < metas.size(); i++) {
            Meta meta = metas.get(i);
            System.out.printf("%d. %s%n", i + 1, meta.getNombre());
            System.out.printf("   Objetivo: $%.2f | Ahorrado: $%.2f | Progreso: %.1f%%%n",
                    meta.getMontoObjetivo(), meta.getAhorroActual(), meta.getProgreso());
            System.out.printf("   Estado: %s%n", meta.isCompleta() ? "✅ Completada" : "⏳ En progreso");
            System.out.printf("   Descripción: %s%n%n", meta.getDescripcion());
        }
    }

    private static void crearMeta() {
        System.out.println("\n=== CREAR NUEVA META ===");

        try {
            System.out.print("Nombre de la meta: ");
            String nombre = scanner.nextLine().trim();

            if (nombre.isEmpty()) {
                System.out.println("❌ El nombre no puede estar vacío.");
                return;
            }

            System.out.print("Monto objetivo: $");
            double montoObjetivo = Double.parseDouble(scanner.nextLine().trim());

            if (montoObjetivo <= 0) {
                System.out.println("❌ El monto objetivo debe ser mayor a cero.");
                return;
            }

            System.out.print("Ahorro inicial: $");
            double ahorroInicial = Double.parseDouble(scanner.nextLine().trim());

            if (ahorroInicial < 0) {
                System.out.println("❌ El ahorro inicial no puede ser negativo.");
                return;
            }

            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine().trim();

            Meta meta = new Meta();
            meta.setUsuarioId(usuarioActual.getId());
            meta.setNombre(nombre);
            meta.setMontoObjetivo(montoObjetivo);
            meta.setAhorroActual(ahorroInicial);
            meta.setDescripcion(descripcion);

            if (controlador.crearMeta(meta)) {
                System.out.println("✅ Meta creada exitosamente!");

                if (montoObjetivo > usuarioActual.getPresupuestoActual() * 2) {
                    System.out.println("\n💡 Consejo: Esta meta es ambiciosa. Considera dividirla en metas más pequeñas.");
                }
            } else {
                System.out.println("❌ Error al crear la meta.");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Por favor ingrese valores numéricos válidos.");
        }
    }

    private static void actualizarAhorroMeta() {
        List<Meta> metas = controlador.obtenerMetasUsuario();

        if (metas.isEmpty()) {
            System.out.println("❌ No tienes metas para actualizar.");
            return;
        }

        verMetas();

        try {
            System.out.print("Seleccione el número de la meta: ");
            int numeroMeta = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (numeroMeta < 0 || numeroMeta >= metas.size()) {
                System.out.println("❌ Número de meta inválido.");
                return;
            }

            Meta metaSeleccionada = metas.get(numeroMeta);
            System.out.printf("Meta seleccionada: %s (ahorro actual: $%.2f)%n",
                    metaSeleccionada.getNombre(), metaSeleccionada.getAhorroActual());

            System.out.print("Nuevo monto de ahorro: $");
            double nuevoAhorro = Double.parseDouble(scanner.nextLine().trim());

            if (nuevoAhorro < 0) {
                System.out.println("❌ El ahorro no puede ser negativo.");
                return;
            }

            if (controlador.actualizarAhorroMeta(metaSeleccionada.getId(), nuevoAhorro)) {
                System.out.println("✅ Ahorro actualizado exitosamente!");

                if (metaSeleccionada.getAhorroActual() >= metaSeleccionada.getMontoObjetivo() &&
                    nuevoAhorro >= metaSeleccionada.getMontoObjetivo()) {
                    System.out.println("🎉 ¡Felicitaciones! Has completado esta meta.");
                }
            } else {
                System.out.println("❌ Error al actualizar el ahorro.");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Por favor ingrese un número válido.");
        }
    }

    private static void eliminarMeta() {
        List<Meta> metas = controlador.obtenerMetasUsuario();

        if (metas.isEmpty()) {
            System.out.println("❌ No tienes metas para eliminar.");
            return;
        }

        verMetas();

        try {
            System.out.print("Seleccione el número de la meta a eliminar: ");
            int numeroMeta = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (numeroMeta < 0 || numeroMeta >= metas.size()) {
                System.out.println("❌ Número de meta inválido.");
                return;
            }

            Meta metaSeleccionada = metas.get(numeroMeta);
            System.out.printf("¿Eliminar la meta '%s'? (s/n): ", metaSeleccionada.getNombre());

            if (scanner.nextLine().toLowerCase().startsWith("s")) {
                if (controlador.eliminarMeta(metaSeleccionada.getId())) {
                    System.out.println("✅ Meta eliminada exitosamente.");
                } else {
                    System.out.println("❌ Error al eliminar la meta.");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Por favor ingrese un número válido.");
        }
    }

    private static void verEstadisticas() {
        System.out.println("\n=== ESTADÍSTICAS FINANCIERAS ===");

        Map<String, Object> estadisticas = controlador.obtenerEstadisticasGenerales();

        System.out.printf("📊 Total de transacciones: %d%n",
                (int) estadisticas.getOrDefault("totalTransacciones", 0));
        System.out.printf("💰 Total ingresos: $%.2f%n",
                (double) estadisticas.getOrDefault("totalIngresos", 0.0));
        System.out.printf("💸 Total gastos: $%.2f%n",
                (double) estadisticas.getOrDefault("totalGastos", 0.0));
        System.out.printf("📈 Balance general: $%.2f%n",
                (double) estadisticas.getOrDefault("balanceGeneral", 0.0));

        System.out.printf("\n🎯 Total de metas: %d%n",
                (int) estadisticas.getOrDefault("totalMetas", 0));
        System.out.printf("✅ Metas completadas: %d%n",
                (int) estadisticas.getOrDefault("metasCompletadas", 0));
        System.out.printf("⏳ Metas pendientes: %d%n",
                (int) estadisticas.getOrDefault("metasPendientes", 0));
        System.out.printf("📊 Progreso promedio: %.1f%%%n",
                (double) estadisticas.getOrDefault("progresoPromedio", 0.0));
    }

    private static void mostrarConsejos() {
        System.out.println("\n=== CONSEJOS FINANCIEROS EDUCATIVOS ===");

        List<String> consejos = controlador.obtenerConsejosEducativos();

        if (consejos.isEmpty()) {
            System.out.println("✅ ¡Excelente! Sigue así.");
            System.out.println("\n💡 RECOMENDACIONES GENERALES:");
            System.out.println("• Registra todas tus transacciones");
            System.out.println("• Establece metas realistas");
            System.out.println("• Revisa tus gastos regularmente");
            System.out.println("• Ahorra al menos el 20% de tus ingresos");
            System.out.println("• Educa tus gastos innecesarios");
        } else {
            System.out.println("💡 CONSEJOS PERSONALIZADOS:");
            for (int i = 0; i < consejos.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, consejos.get(i));
            }
        }

        System.out.println("\n📚 RECURSOS EDUCATIVOS:");
        System.out.println("• El ahorro consistente es la clave del éxito financiero");
        System.out.println("• Conoce la regla 50/30/20: 50% necesidades, 30% deseos, 20% ahorro");
        System.out.println("• Revisa tus finanzas semanalmente para mantener el control");
        System.out.println("• Establece metas SMART: Específicas, Medibles, Alcanzables, Relevantes, con Tiempo");
    }

    private static void verPerfil() {
        System.out.println("\n=== PERFIL DE USUARIO ===");
        System.out.printf("👤 Nombre: %s%n", usuarioActual.getNombre());
        System.out.printf("🎂 Edad: %d años%n", usuarioActual.getEdad());
        System.out.printf("📋 Tipo de uso: %s%n", usuarioActual.getTipoUso());
        System.out.printf("💰 Presupuesto inicial: $%.2f%n", usuarioActual.getPresupuestoInicial());
        System.out.printf("📊 Saldo actual: $%.2f%n", usuarioActual.getPresupuestoActual());

        if (usuarioActual.getFechaCreacion() != null) {
            System.out.printf("📅 Miembro desde: %s%n",
                    usuarioActual.getFechaCreacion().toLocalDate().toString());
        }
    }
}