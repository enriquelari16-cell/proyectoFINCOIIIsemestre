# 🎯 Diagrama de Casos de Uso UML

## Casos de Uso Principales

```mermaid
useCaseDiagram
    %% Actores
    actor "Usuario Nuevo" as UN
    actor "Usuario Registrado" as UR
    actor "Sistema" as SYS

    %% Casos de Uso de Autenticación
    rectangle "Autenticación" {
        UN --> (Registrarse)
        UR --> (Iniciar Sesión)
        SYS --> (Validar Credenciales)
        SYS --> (Crear Cuenta)
    }

    %% Casos de Uso de Gestión Financiera
    rectangle "Gestión Financiera" {
        UR --> (Registrar Transacción)
        UR --> (Ver Transacciones)
        UR --> (Eliminar Transacción)
        UR --> (Ver Saldo Actual)
        UR --> (Filtrar Transacciones)
        SYS --> (Actualizar Presupuesto)
        SYS --> (Validar Fondos)
    }

    %% Casos de Uso de Metas Financieras
    rectangle "Metas Financieras" {
        UR --> (Crear Meta)
        UR --> (Ver Metas)
        UR --> (Actualizar Ahorro)
        UR --> (Eliminar Meta)
        UR --> (Ver Progreso Meta)
        SYS --> (Calcular Progreso)
        SYS --> (Notificar Meta Completada)
    }

    %% Casos de Uso Educativos
    rectangle "Educación Financiera" {
        UR --> (Ver Consejos)
        UR --> (Ver Estadísticas)
        UR --> (Obtener Recomendaciones)
        SYS --> (Analizar Comportamiento)
        SYS --> (Generar Consejos Personalizados)
        SYS --> (Calcular Estadísticas)
    }

    %% Relaciones entre casos de uso
    (Registrar Transacción) --> (Actualizar Presupuesto)
    (Eliminar Transacción) --> (Actualizar Presupuesto)
    (Registrar Transacción) --> (Validar Fondos)
    (Actualizar Ahorro) --> (Calcular Progreso)
    (Calcular Progreso) --> (Notificar Meta Completada)
    (Ver Estadísticas) --> (Calcular Estadísticas)
    (Ver Consejos) --> (Analizar Comportamiento)
    (Analizar Comportamiento) --> (Generar Consejos Personalizados)
```

## Detalle de Casos de Uso

### 👤 Autenticación y Registro

#### Caso de Uso: Registrarse
**Actor:** Usuario Nuevo
**Descripción:** Un usuario nuevo crea una cuenta en el sistema
**Precondiciones:**
- Usuario no tiene cuenta registrada
**Postcondiciones:**
- Cuenta creada exitosamente
- Usuario puede iniciar sesión
**Flujo Principal:**
1. Usuario selecciona "Crear Cuenta"
2. Sistema muestra formulario de registro
3. Usuario ingresa datos personales
4. Usuario define presupuesto inicial
5. Sistema valida datos
6. Sistema crea cuenta
7. Sistema confirma registro exitoso

#### Caso de Uso: Iniciar Sesión
**Actor:** Usuario Registrado
**Descripción:** Usuario accede al sistema con credenciales
**Precondiciones:**
- Usuario tiene cuenta registrada
**Postcondiciones:**
- Usuario autenticado en el sistema
**Flujo Principal:**
1. Usuario ingresa nombre y contraseña
2. Sistema valida credenciales
3. Sistema carga datos del usuario
4. Sistema muestra interfaz principal

### 💰 Gestión de Transacciones

#### Caso de Uso: Registrar Transacción
**Actor:** Usuario Registrado
**Descripción:** Usuario registra un ingreso o gasto
**Precondiciones:**
- Usuario autenticado
**Postcondiciones:**
- Transacción guardada
- Presupuesto actualizado
**Flujo Principal:**
1. Usuario selecciona "Nueva Transacción"
2. Sistema muestra formulario
3. Usuario selecciona tipo (Ingreso/Gasto)
4. Usuario ingresa monto y descripción
5. Sistema valida fondos (para gastos)
6. Sistema guarda transacción
7. Sistema actualiza presupuesto
8. Sistema muestra confirmación

#### Caso de Uso: Ver Transacciones
**Actor:** Usuario Registrado
**Descripción:** Usuario consulta su historial de transacciones
**Precondiciones:**
- Usuario autenticado
**Postcondiciones:**
- Lista de transacciones mostrada
**Flujo Principal:**
1. Usuario selecciona ver transacciones
2. Sistema obtiene transacciones del usuario
3. Sistema muestra lista paginada
4. Usuario puede filtrar por tipo/monto

### 🎯 Gestión de Metas

#### Caso de Uso: Crear Meta
**Actor:** Usuario Registrado
**Descripción:** Usuario establece un objetivo de ahorro
**Precondiciones:**
- Usuario autenticado
**Postcondiciones:**
- Meta creada y guardada
**Flujo Principal:**
1. Usuario selecciona "Nueva Meta"
2. Sistema muestra formulario
3. Usuario ingresa nombre, objetivo, ahorro inicial
4. Sistema valida datos
5. Sistema guarda meta
6. Sistema muestra confirmación

#### Caso de Uso: Actualizar Ahorro
**Actor:** Usuario Registrado
**Descripción:** Usuario actualiza el progreso de una meta
**Precondiciones:**
- Usuario tiene metas creadas
**Postcondiciones:**
- Ahorro de meta actualizado
**Flujo Principal:**
1. Usuario selecciona meta
2. Usuario ingresa nuevo monto de ahorro
3. Sistema actualiza meta
4. Sistema recalcula progreso
5. Sistema notifica si meta completada

### 📊 Educación Financiera

#### Caso de Uso: Ver Consejos
**Actor:** Usuario Registrado
**Descripción:** Usuario recibe consejos personalizados
**Precondiciones:**
- Usuario tiene actividad registrada
**Postcondiciones:**
- Consejos mostrados al usuario
**Flujo Principal:**
1. Usuario solicita consejos
2. Sistema analiza comportamiento financiero
3. Sistema genera consejos personalizados
4. Sistema muestra consejos relevantes

#### Caso de Uso: Ver Estadísticas
**Actor:** Usuario Registrado
**Descripción:** Usuario consulta estadísticas financieras
**Precondiciones:**
- Usuario tiene transacciones registradas
**Postcondiciones:**
- Estadísticas mostradas
**Flujo Principal:**
1. Usuario solicita estadísticas
2. Sistema calcula métricas financieras
3. Sistema genera reportes
4. Sistema muestra gráficos/estadísticas

## Escenarios Alternativos

### Registro Fallido
1. Usuario ingresa datos inválidos
2. Sistema muestra errores específicos
3. Usuario corrige datos
4. Sistema reintenta validación

### Transacción sin Fondos
1. Usuario intenta gasto mayor al saldo
2. Sistema advierte sobre saldo negativo
3. Usuario confirma o cancela
4. Sistema procesa según decisión

### Meta Sobrepasada
1. Usuario ingresa ahorro mayor al objetivo
2. Sistema ajusta automáticamente
3. Sistema marca meta como completada
4. Sistema felicita al usuario

## Requisitos No Funcionales

### Rendimiento
- Tiempo de respuesta < 2 segundos para operaciones comunes
- Soporte para hasta 1000 transacciones por usuario
- Cache en memoria para datos frecuentes

### Usabilidad
- Interfaz intuitiva sin necesidad de capacitación
- Mensajes de ayuda contextuales
- Validación en tiempo real de entradas

### Seguridad
- Contraseñas hasheadas (futuro)
- Validación de sesiones
- Control de acceso por usuario

### Educativo
- Consejos basados en comportamiento real
- Información financiera precisa
- Progreso medible en educación financiera