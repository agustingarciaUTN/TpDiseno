@echo off
echo ================================================
echo   Verificador de Conexión Backend
echo ================================================
echo.

echo Verificando si el backend está corriendo...
curl -s http://localhost:8080/api/huespedes/buscar -H "Content-Type: application/json" -d "{}" >nul 2>&1

if %errorlevel% equ 0 (
    echo [OK] Backend está corriendo en http://localhost:8080
) else (
    echo [ERROR] Backend NO está corriendo o no responde
    echo.
    echo Para iniciar el backend:
    echo   cd c:\Facu\Diseno\TPCode\TpDiseno
    echo   mvnw.cmd spring-boot:run
)

echo.
echo Verificando si el frontend está corriendo...
curl -s http://localhost:3000 >nul 2>&1

if %errorlevel% equ 0 (
    echo [OK] Frontend está corriendo en http://localhost:3000
) else (
    echo [ERROR] Frontend NO está corriendo o no responde
    echo.
    echo Para iniciar el frontend:
    echo   cd c:\Facu\Diseno\TPCode\TpDiseno\frontend-hotel
    echo   npm run dev
)

echo.
echo ================================================
pause
