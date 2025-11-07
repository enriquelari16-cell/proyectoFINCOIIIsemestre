# 💰 Gestor Financiero Educativo

Una aplicación completa para la gestión financiera personal con enfoque educativo, desarrollada en Java con arquitectura MVC.

## 🚀 Características Principales

### Interfaz Gráfica (Swing)
- **Interfaz intuitiva y moderna** con pestañas organizadas
- **Panel de consejos educativos** personalizados
- **Gestión visual de metas** con indicadores de progreso
- **Estadísticas detalladas** con gráficos y reportes
- **Validaciones en tiempo real** y retroalimentación inmediata

### Interfaz de Terminal
- **Menú interactivo completo** con navegación intuitiva
- **Consejos educativos integrados** basados en comportamiento financiero
- **Gestión completa de transacciones** y metas
- **Estadísticas detalladas** en formato texto
- **Validaciones robustas** y manejo de errores

### Arquitectura MVC
- **Controlador centralizado** (`FinanzasController`) que maneja la lógica de negocio
- **Colecciones Java** (ArrayList, HashMap, HashSet) para gestión en memoria
- **Separación clara** entre vista, controlador y modelo
- **DAO pattern** para persistencia en base de datos MySQL

### Funcionalidades Educativas
- **Consejos personalizados** basados en hábitos financieros
- **Sistema de metas** con seguimiento de progreso
- **Alertas inteligentes** para gastos altos
- **Educación financiera integrada** en cada interacción
- **Estadísticas comparativas** y análisis de tendencias

## 🏗️ Arquitectura del Proyecto

```
com/
├── finanzas/
│   ├── Main.java              # Aplicación gráfica principal
│   ├── MainTerminal.java      # Aplicación de terminal
│   └── controlador/
│       └── FinanzasController.java  # Controlador principal
├── modelo/
│   ├── Usuario.java
│   ├── Transaccion.java
│   ├── Meta.java
│   └── Categoria.java
├── dao/
│   ├── ConexionDB.java
│   ├── UsuarioDAO.java
│   ├── TransaccionDAO.java
│   └── MetaDAO.java
└── vista/
    ├── LoginFrame.java
    ├── MainFrame.java
    ├── NuevaTransaccionDialog.java
    ├── GestionarMetasDialog.java
    ├── RegistroDialog.java
    └── EducationalTipsPanel.java
```

## 🛠️ Tecnologías Utilizadas

- **Java 8+** - Lenguaje de programación principal
- **Swing** - Framework para interfaz gráfica
- **MySQL** - Base de datos relacional
- **JDBC** - Conexión a base de datos
- **Colecciones Java** - Estructuras de datos en memoria
- **Git** - Control de versiones

## 📋 Requisitos del Sistema

- **Java JDK 8** o superior
- **MySQL Server** 5.7 o superior
- **Conector MySQL JDBC** (incluido en `com/lib/`)
- **Sistema operativo**: Windows, Linux o macOS

## 🚀 Instalación y Ejecución

### 1. Configuración de la Base de Datos

```sql
-- Crear base de datos
CREATE DATABASE finanzas_personales;

-- Crear tablas (ejecutar los scripts SQL incluidos)
-- Las tablas se crean automáticamente al ejecutar la aplicación
```

### 2. Configuración de Conexión

Editar `com/finanzas/dao/ConexionDB.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/finanzas_personales";
private static final String USUARIO = "tu_usuario";
private static final String CONTRASENA = "tu_contraseña";
```

### 3. Configuración del Conector MySQL

**Importante:** El proyecto incluye compatibilidad con múltiples versiones del conector MySQL.

#### Opción A: Usar MySQL Connector/J 8.0.33 (Recomendado)
1. Descarga desde: https://dev.mysql.com/downloads/connector/j/
2. Selecciona "Platform Independent"
3. Extrae `mysql-connector-java-8.0.33.jar`
4. Colócalo en `com/lib/mysql-connector-java-8.0.33.jar`

#### Opción B: Usar versión existente
- El proyecto ya incluye configuración compatible con MySQL Connector/J 9.x
- Si tienes problemas, usa la Opción A

### 4. Compilación

```bash
# Compilar aplicación gráfica
javac -cp "com/lib/mysql-connector-java-8.0.33.jar;." com/finanzas/Main.java com/finanzas/controlador/*.java com/finanzas/modelo/*.java com/finanzas/dao/*.java com/finanzas/vista/*.java

# Compilar aplicación de terminal
javac -cp "com/lib/mysql-connector-java-8.0.33.jar;." com/finanzas/MainTerminal.java com/finanzas/controlador/*.java com/finanzas/modelo/*.java com/finanzas/dao/*.java com/finanzas/vista/*.java
```

### 5. Ejecución

```bash
# Ejecutar aplicación gráfica
java -cp ".;com/lib/mysql-connector-java-8.0.33.jar" finanzas.Main

# Ejecutar aplicación de terminal
java -cp ".;com/lib/mysql-connector-java-8.0.33.jar" finanzas.MainTerminal

# Probar conexión a base de datos
java -cp ".;com/lib/mysql-connector-java-8.0.33.jar" finanzas.TestConexionSimple
```

## 📊 Funcionalidades Detalladas

### Gestión de Usuarios
- ✅ Registro de nuevos usuarios con validaciones
- ✅ Autenticación segura
- ✅ Perfiles personalizados por tipo de uso

### Transacciones Financieras
- ✅ Registro de ingresos y gastos
- ✅ Categorización automática
- ✅ Validación de fondos disponibles
- ✅ Historial completo con filtros

### Sistema de Metas
- ✅ Creación de metas de ahorro
- ✅ Seguimiento visual del progreso
- ✅ Notificaciones de metas completadas
- ✅ Actualización de ahorros

### Estadísticas y Reportes
- ✅ Balance general y tendencias
- ✅ Análisis de gastos por categorías
- ✅ Progreso de metas
- ✅ Reportes comparativos

### Educación Financiera
- ✅ Consejos personalizados basados en comportamiento
- ✅ Alertas para gastos altos
- ✅ Recomendaciones de ahorro
- ✅ Información educativa integrada

## 🎯 Casos de Uso

### Para Usuarios Principantes
- Interfaz simple e intuitiva
- Consejos básicos de educación financiera
- Guías paso a paso para gestión básica

### Para Usuarios Avanzados
- Estadísticas detalladas y comparativas
- Gestión avanzada de metas
- Análisis de tendencias financieras
- Categorización personalizada

### Para Educación
- Ejemplos prácticos de conceptos financieros
- Seguimiento de progreso de aprendizaje
- Retroalimentación educativa personalizada

## 🔧 Mantenimiento y Desarrollo

### Agregar Nuevas Funcionalidades
1. Crear clases en el paquete correspondiente (modelo/vista/controlador)
2. Implementar métodos en el controlador
3. Actualizar la interfaz gráfica si es necesario
4. Agregar validaciones y consejos educativos

### Modificar la Base de Datos
1. Actualizar scripts SQL
2. Modificar DAOs correspondientes
3. Actualizar el controlador si es necesario
4. Probar cambios en ambas interfaces

## 📈 Mejoras Futuras

- [ ] **Aplicación Web** con Spring Boot
- [ ] **API REST** para integración móvil
- [ ] **Gráficos avanzados** con JFreeChart
- [ ] **Exportación de reportes** PDF/Excel
- [ ] **Sincronización en la nube**
- [ ] **Presupuestos por categorías**
- [ ] **Recordatorios automáticos**
- [ ] **Análisis predictivo** de gastos

## 🤝 Contribución

1. Fork el proyecto
2. Crear rama para nueva funcionalidad (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -am 'Agrega nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver archivo `LICENSE` para más detalles.

## 📞 Soporte

Para soporte técnico o preguntas sobre el proyecto:
- Crear issue en GitHub
- Revisar documentación en el código
- Consultar ejemplos de uso incluidos

---

**Desarrollado con ❤️ para promover la educación financiera personal**