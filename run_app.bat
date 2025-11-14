@echo off
title Gestor Financiero Educativo - Terminal
echo.
echo ==============================================================
echo    💰 GESTOR FINANCIERO EDUCATIVO - TERMINAL 💰
echo ==============================================================
echo.
echo 🚀 Iniciando aplicacion...
echo.
echo NOTA: La aplicacion usara la version terminal que es completamente funcional.
echo Esta version incluye todas las funcionalidades financieras educativas.
echo.
echo Para usar la aplicacion:
echo 1. Ingresa credenciales existentes o crea una cuenta nueva
echo 2. Gestiona tus transacciones, metas y estadisticas
echo 3. Recibe consejos financieros personalizados
echo.
echo Presiona cualquier tecla para continuar...
pause >nul

echo.
echo ✅ Compilando aplicacion...
javac -cp "com/lib/mysql-connector-j-9.3.0.jar" com/finanzas/MainTerminal.java

echo.
echo 🚀 Ejecutando aplicacion...
java -cp ".:com/lib/mysql-connector-j-9.3.0.jar" finanzas.MainTerminal

echo.
echo Aplicacion finalizada. Presiona cualquier tecla para salir...
pause >nul
