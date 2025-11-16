@echo off
echo ========================================
echo    VERIFICADOR DE ERRORES - MAPEO
echo ========================================
echo.

cd /d "%~dp0"

echo [1/5] Verificando archivos clave...
if exist "app\src\main\java\com\example\mapeo\MainActivity.kt" (
    echo [OK] MainActivity.kt existe
) else (
    echo [ERROR] MainActivity.kt NO encontrado
    goto :end
)

if exist "app\src\main\res\layout\activity_main.xml" (
    echo [OK] activity_main.xml existe
) else (
    echo [ERROR] activity_main.xml NO encontrado
    goto :end
)

if exist "app\build.gradle.kts" (
    echo [OK] build.gradle.kts existe
) else (
    echo [ERROR] build.gradle.kts NO encontrado
    goto :end
)

echo.
echo [2/5] Limpiando proyecto...
call gradlew.bat clean > nul 2>&1
if errorlevel 1 (
    echo [ERROR] Falló al limpiar
) else (
    echo [OK] Proyecto limpiado
)

echo.
echo [3/5] Verificando dependencias...
call gradlew.bat dependencies --configuration implementation 2>&1 | findstr /C:"osmdroid" /C:"preference" /C:"appcompat" /C:"material" > temp_deps.txt
if exist temp_deps.txt (
    echo [OK] Dependencias principales encontradas:
    type temp_deps.txt
    del temp_deps.txt
) else (
    echo [WARNING] No se pudieron verificar dependencias
)

echo.
echo [4/5] Compilando proyecto (esto puede tardar)...
call gradlew.bat assembleDebug --stacktrace > build_log.txt 2>&1
if errorlevel 1 (
    echo [ERROR] La compilación FALLÓ
    echo.
    echo Mostrando últimas 30 líneas del log:
    powershell -Command "Get-Content build_log.txt | Select-Object -Last 30"
    echo.
    echo Log completo guardado en: build_log.txt
    goto :end
) else (
    echo [OK] Compilación EXITOSA
    echo.
    echo APK generado en:
    dir /s /b app\build\outputs\apk\debug\*.apk 2>nul
)

echo.
echo [5/5] Resumen:
echo ----------------------------------------
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo RESULTADO: COMPILACIÓN EXITOSA
    echo.
    echo La app está lista para ejecutarse.
    echo Puedes instalarla con:
    echo   adb install -r app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo O ejecutarla desde Android Studio
) else (
    echo RESULTADO: COMPILACIÓN FALLIDA
    echo.
    echo Revisa el archivo build_log.txt para más detalles
    echo.
    echo Errores comunes:
    echo - Falta Android SDK
    echo - Dependencias no sincronizadas
    echo - Error de sintaxis en código
    echo.
    echo Solución:
    echo 1. Abre Android Studio
    echo 2. File ^> Sync Project with Gradle Files
    echo 3. Revisa errores en el panel Build
)

:end
echo.
echo ========================================
pause
