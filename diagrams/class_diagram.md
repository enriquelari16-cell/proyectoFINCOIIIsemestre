# 📋 Diagrama de Clases UML

## Arquitectura General

```mermaid
classDiagram
    %% Controlador Principal
    class FinanzasController {
        -UsuarioDAO usuarioDAO
        -TransaccionDAO transaccionDAO
        -MetaDAO metaDAO
        -Map~Integer,Usuario~ usuariosCache
        -Map~Integer,List~Transaccion~~ transaccionesPorUsuario
        -Map~Integer,List~Meta~~ metasPorUsuario
        -Usuario usuarioActual
        +registrarUsuario(Usuario): boolean
        +autenticarUsuario(String, String): boolean
        +getUsuarioActual(): Usuario
        +cerrarSesion(): void
        +crearTransaccion(Transaccion): boolean
        +obtenerTransaccionesFiltradas(String, double): List~Transaccion~
        +eliminarTransaccion(int): boolean
        +crearMeta(Meta): boolean
        +obtenerMetasUsuario(): List~Meta~
        +actualizarAhorroMeta(int, double): boolean
        +eliminarMeta(int): boolean
        +obtenerEstadisticasGenerales(): Map~String,Object~
        +obtenerConsejosEducativos(): List~String~
    }

    %% Modelos de Datos
    class Usuario {
        -int id
        -String nombre
        -byte edad
        -String tipoUso
        -String contrasena
        -double presupuestoInicial
        -double presupuestoActual
        -LocalDateTime fechaCreacion
        +getters/setters
    }

    class Transaccion {
        -int id
        -int usuarioId
        -String tipo
        -double monto
        -String descripcion
        -LocalDate fecha
        -double saldoDespues
        -int categoriaId
        +isIngreso(): boolean
        +isGasto(): boolean
        +getters/setters
    }

    class Meta {
        -int id
        -int usuarioId
        -String nombre
        -double montoObjetivo
        -double ahorroActual
        -String descripcion
        +getProgreso(): double
        +isCompleta(): boolean
        +getters/setters
    }

    class Categoria {
        -int id
        -int usuarioId
        -String nombre
        -double presupuesto
        +getters/setters
    }

    %% DAOs (Data Access Objects)
    class UsuarioDAO {
        +existeUsuario(String): boolean
        +registrarUsuario(Usuario): boolean
        +autenticarUsuario(String, String): Usuario
        +obtenerPresupuestoActual(int): double
        +actualizarPresupuesto(int, double): boolean
        +obtenerUsuarioPorId(int): Usuario
        +actualizarUsuario(Usuario): boolean
        +cambiarContrasena(int, String): boolean
    }

    class TransaccionDAO {
        +crearTransaccion(Transaccion): boolean
        +obtenerTransaccionesPorUsuario(int, String, double): List~Transaccion~
        +eliminarTransaccion(int): boolean
        +obtenerEstadisticasGastos(int, LocalDate, LocalDate): Map~String,Object~
        +obtenerEstadisticasGenerales(int): Map~String,Object~
        +obtenerComparativoIngresosGastos(int, String, int): List~Map~
        +obtenerDatosMensuales(int, int): List~Map~
    }

    class MetaDAO {
        +crearMeta(Meta): boolean
        +obtenerMetasPorUsuario(int): List~Meta~
        +actualizarAhorro(int, double): boolean
        +eliminarMeta(int): boolean
        +obtenerEstadisticasMetas(int): Map~String,Object~
    }

    class ConexionDB {
        -static final String URL
        -static final String USUARIO
        -static final String CONTRASENA
        +static getConnection(): Connection
    }

    %% Vistas (Swing)
    class MainFrame {
        -FinanzasController controlador
        -Usuario usuarioActual
        -JTable tablaTransacciones
        -JTable tablaMetas
        -JTabbedPane tabbedPane
        +MainFrame(Usuario)
        -initComponents(): void
        -cargarDatos(): void
        -cargarTransacciones(): void
        -cargarMetas(): void
        -cargarEstadisticas(): void
        -cargarConsejos(): void
    }

    class LoginFrame {
        -JTextField txtNombre
        -JPasswordField txtContrasena
        -JButton btnLogin
        -UsuarioDAO usuarioDAO
        +LoginFrame()
        -initComponents(): void
        -iniciarSesion(): void
        -mostrarRegistro(): void
    }

    class NuevaTransaccionDialog {
        -FinanzasController controlador
        -int usuarioId
        -JComboBox~String~ comboTipo
        -JComboBox~Categoria~ comboCategoria
        -JTextField txtMonto
        -JTextField txtDescripcion
        +NuevaTransaccionDialog(Frame, FinanzasController, int)
        -guardarTransaccion(): void
        -validarMonto(): void
    }

    class GestionarMetasDialog {
        -FinanzasController controlador
        -JTable tableMetas
        -DefaultTableModel tableModel
        -JTextField txtNombre
        -JTextField txtMontoObjetivo
        -JTextField txtAhorroActual
        -JTextArea txtDescripcion
        +GestionarMetasDialog(Frame, FinanzasController)
        -agregarMeta(ActionEvent): void
        -actualizarAhorro(ActionEvent): void
        -eliminarMeta(ActionEvent): void
        -cargarMetas(): void
    }

    class RegistroDialog {
        -FinanzasController controlador
        -JFrame parent
        -JTextField txtNombre
        -JPasswordField txtContrasena
        -JPasswordField txtConfirmarContrasena
        -JTextField txtEdad
        -JComboBox~String~ comboTipoUso
        -JTextField txtPresupuestoInicial
        +RegistroDialog(Frame, FinanzasController)
        -registrarUsuario(ActionEvent): void
        -validarContrasenas(): void
    }

    class EducationalTipsPanel {
        -FinanzasController controlador
        -JTextArea txtConsejos
        -JButton btnActualizarConsejos
        -JButton btnMostrarEstadisticas
        +EducationalTipsPanel(FinanzasController)
        -cargarConsejos(): void
        -mostrarEstadisticas(): void
    }

    %% Relaciones
    FinanzasController --> UsuarioDAO
    FinanzasController --> TransaccionDAO
    FinanzasController --> MetaDAO
    FinanzasController --> Usuario
    FinanzasController --> Transaccion
    FinanzasController --> Meta

    UsuarioDAO --> ConexionDB
    TransaccionDAO --> ConexionDB
    MetaDAO --> ConexionDB

    MainFrame --> FinanzasController
    LoginFrame --> UsuarioDAO
    NuevaTransaccionDialog --> FinanzasController
    GestionarMetasDialog --> FinanzasController
    RegistroDialog --> FinanzasController
    EducationalTipsPanel --> FinanzasController

    UsuarioDAO --> Usuario
    TransaccionDAO --> Transaccion
    MetaDAO --> Meta

    Transaccion --> Usuario
    Meta --> Usuario
    Categoria --> Usuario
```

## Relaciones de Dependencia

- **FinanzasController** es el núcleo de la aplicación, coordinando todas las operaciones
- **DAOs** manejan la persistencia, accediendo a la base de datos a través de **ConexionDB**
- **Vistas** dependen del **FinanzasController** para lógica de negocio
- **Modelos** representan las entidades de datos y son usados por todos los componentes

## Colecciones Utilizadas

```java
// Cache de usuarios
Map<Integer, Usuario> usuariosCache

// Transacciones por usuario
Map<Integer, List<Transaccion>> transaccionesPorUsuario

// Metas por usuario
Map<Integer, List<Meta>> metasPorUsuario

// Categorías por usuario
Map<Integer, List<Categoria>> categoriasPorUsuario
```

## Principios SOLID Aplicados

1. **S** - Single Responsibility: Cada clase tiene una responsabilidad específica
2. **O** - Open/Closed: El controlador puede extenderse sin modificarse
3. **L** - Liskov Substitution: Las vistas pueden intercambiarse
4. **I** - Interface Segregation: Interfaces específicas por funcionalidad
5. **D** - Dependency Inversion: Dependencia de abstracciones, no de concretos