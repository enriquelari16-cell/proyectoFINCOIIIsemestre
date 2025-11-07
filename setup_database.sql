-- Script de configuración de base de datos para el Gestor Financiero Educativo
-- Ejecutar este script en MySQL para crear la base de datos y las tablas necesarias

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS finanzas_personales
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE finanzas_personales;

-- Crear tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    edad TINYINT NOT NULL CHECK (edad > 0 AND edad < 120),
    tipo_uso ENUM('Personal', 'Familiar', 'Empresarial') NOT NULL DEFAULT 'Personal',
    contrasena VARCHAR(255) NOT NULL,
    presupuesto_inicial DECIMAL(15,2) NOT NULL DEFAULT 0.00 CHECK (presupuesto_inicial >= 0),
    presupuesto_actual DECIMAL(15,2) NOT NULL DEFAULT 0.00 CHECK (presupuesto_actual >= 0),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nombre (nombre)
);

-- Crear tabla de categorías
CREATE TABLE IF NOT EXISTS categorias (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    presupuesto DECIMAL(15,2) NOT NULL DEFAULT 0.00 CHECK (presupuesto >= 0),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_usuario_id (usuario_id)
);

-- Crear tabla de transacciones
CREATE TABLE IF NOT EXISTS transacciones (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    tipo ENUM('Ingreso', 'Gasto') NOT NULL,
    monto DECIMAL(15,2) NOT NULL CHECK (monto > 0),
    descripcion TEXT,
    fecha DATE NOT NULL DEFAULT (CURRENT_DATE),
    saldo_despues DECIMAL(15,2) NOT NULL,
    categoria_id INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL,
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_fecha (fecha),
    INDEX idx_tipo (tipo)
);

-- Crear tabla de metas
CREATE TABLE IF NOT EXISTS metas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    monto_objetivo DECIMAL(15,2) NOT NULL CHECK (monto_objetivo > 0),
    ahorro_actual DECIMAL(15,2) NOT NULL DEFAULT 0.00 CHECK (ahorro_actual >= 0),
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_usuario_id (usuario_id)
);

-- Crear usuario de aplicación (opcional - solo si no existe)
-- Cambia 'tu_password' por una contraseña segura
-- GRANT ALL PRIVILEGES ON finanzas_personales.* TO 'finanzas_user'@'localhost' IDENTIFIED BY 'tu_password';
-- FLUSH PRIVILEGES;

-- Insertar datos de ejemplo (opcional)
-- Usuario de prueba
INSERT IGNORE INTO usuarios (nombre, edad, tipo_uso, contrasena, presupuesto_inicial, presupuesto_actual)
VALUES ('usuario_prueba', 25, 'Personal', '123456', 10000.00, 10000.00);

-- Categorías de ejemplo
INSERT IGNORE INTO categorias (usuario_id, nombre, presupuesto)
SELECT u.id, 'Alimentación', 2000.00 FROM usuarios u WHERE u.nombre = 'usuario_prueba'
UNION ALL
SELECT u.id, 'Transporte', 1000.00 FROM usuarios u WHERE u.nombre = 'usuario_prueba'
UNION ALL
SELECT u.id, 'Entretenimiento', 800.00 FROM usuarios u WHERE u.nombre = 'usuario_prueba';

-- Transacciones de ejemplo
INSERT IGNORE INTO transacciones (usuario_id, tipo, monto, descripcion, fecha, saldo_despues)
SELECT u.id, 'Gasto', 150.50, 'Compra en supermercado', CURRENT_DATE - INTERVAL 2 DAY, u.presupuesto_actual - 150.50
FROM usuarios u WHERE u.nombre = 'usuario_prueba'
UNION ALL
SELECT u.id, 'Ingreso', 5000.00, 'Salario mensual', CURRENT_DATE - INTERVAL 1 DAY, u.presupuesto_actual - 150.50 + 5000.00
FROM usuarios u WHERE u.nombre = 'usuario_prueba';

-- Actualizar presupuesto después de transacciones
UPDATE usuarios SET presupuesto_actual = presupuesto_actual - 150.50 + 5000.00 WHERE nombre = 'usuario_prueba';

-- Meta de ejemplo
INSERT IGNORE INTO metas (usuario_id, nombre, monto_objetivo, ahorro_actual, descripcion)
SELECT u.id, 'Fondo de emergencia', 15000.00, 2500.00, 'Ahorrar 3 meses de gastos'
FROM usuarios u WHERE u.nombre = 'usuario_prueba';

-- Mostrar resumen de configuración
SELECT
    'Base de datos configurada correctamente' as Estado,
    (SELECT COUNT(*) FROM usuarios) as Usuarios,
    (SELECT COUNT(*) FROM categorias) as Categorias,
    (SELECT COUNT(*) FROM transacciones) as Transacciones,
    (SELECT COUNT(*) FROM metas) as Metas;

-- Mostrar instrucciones finales
SELECT '🎉 Base de datos configurada exitosamente!' as Mensaje UNION ALL
SELECT '📝 Usuario de prueba: usuario_prueba / 123456' UNION ALL
SELECT '🚀 La aplicación está lista para usar' UNION ALL
SELECT '💡 Recuerda cambiar la contraseña en producción';