@echo off
cd /d "%~dp0"
echo Compilando proyecto MAPEO...
echo.
call gradlew.bat clean assembleDebug --stacktrace
echo.
echo Proceso completado.
pause
