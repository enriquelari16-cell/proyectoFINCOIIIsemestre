#!/bin/bash

echo ""
echo "=============================================================="
echo "    💰 GESTOR FINANCIERO EDUCATIVO - TERMINAL 💰"
echo "=============================================================="
echo ""
echo "🚀 Iniciando aplicación..."
echo ""
echo "NOTA: La aplicación usará la versión terminal que es completamente funcional."
echo "Esta versión incluye todas las funcionalidades financieras educativas."
echo ""
echo "Para usar la aplicación:"
echo "1. Ingresa credenciales existentes o crea una cuenta nueva"
echo "2. Gestiona tus transacciones, metas y estadísticas"
echo "3. Recibe consejos financieros personalizados"
echo ""
read -p "Presiona Enter para continuar..."

echo ""
echo "✅ Compilando aplicación..."
javac -cp "com/lib/mysql-connector-j-9.3.0.jar" com/finanzas/MainTerminal.java

echo ""
echo "🚀 Ejecutando aplicación..."
java -cp ".:com/lib/mysql-connector-j-9.3.0.jar" finanzas.MainTerminal

echo ""
echo "Aplicación finalizada. Presiona Enter para salir..."
read
