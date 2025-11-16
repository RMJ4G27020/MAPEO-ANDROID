# GUÍA DE DEBUGGING - MainActivity

## CAMBIOS IMPLEMENTADOS

### 1. Sistema de Logging Completo
- Agregado import: `android.util.Log`
- Companion object con TAG para identificar logs
- Logs en TODAS las funciones principales:
  * onCreate(): Inicio y finalización
  * setupMap(): Configuración del mapa
  * setupMapTypeSpinner(): Configuración del selector
  * searchAddress(): Búsqueda de direcciones
  * displayLocation(): Mostrar ubicación
  * Ciclo de vida: onResume(), onPause(), onDestroy()

### 2. Manejo de Errores Mejorado
- Try-catch blocks en todas las funciones críticas
- Mensajes de error detallados en Toast
- Stack traces completos en Logcat

### 3. Correcciones de Código
- Agregado companion object con TAG
- Try-catch en onCreate
- Try-catch en setupMap
- Try-catch en setupMapTypeSpinner
- Try-catch en updateMarkerColor
- Try-catch en searchAddress y displayLocation
- Try-catch en requestLocationPermission
- Try-catch en onResume y onPause
- Agregado onDestroy() con log

## CÓMO VER LOS LOGS EN ANDROID STUDIO

### Opción 1: Logcat (Recomendado)
1. Abre Android Studio
2. Ve a la pestaña "Logcat" (parte inferior)
3. En el filtro, escribe: `tag:MainActivity`
4. Ejecuta la app
5. Verás TODOS los logs con timestamps

### Opción 2: Filtro por Nivel
- Error (rojo): `Log.e()` - Errores críticos
- Warning (naranja): `Log.w()` - Advertencias
- Debug (azul): `Log.d()` - Información de debug
- Info (verde): `Log.i()` - Información general

### Opción 3: Comando ADB
Abre una terminal y ejecuta:
```bash
adb logcat | findstr "MainActivity"
```

## SECUENCIA DE LOGS ESPERADA

### Al iniciar la app:
```
D/MainActivity: onCreate iniciado
D/MainActivity: Configuración OSMDroid completada
D/MainActivity: View binding completado
D/MainActivity: MapView configurado correctamente
D/MainActivity: Spinner configurado correctamente
D/MainActivity: Solicitando permisos de ubicación (o "ya concedidos")
D/MainActivity: onCreate completado exitosamente
D/MainActivity: onResume - MapView resumido
```

### Al buscar una dirección:
```
D/MainActivity: Botón de búsqueda presionado
D/MainActivity: Buscando dirección: [texto ingresado]
D/MainActivity: Usando Geocoder API legacy (o API 33+)
D/MainActivity: Dirección encontrada: [detalles]
D/MainActivity: Mostrando ubicación: Lat=X, Lng=Y
D/MainActivity: Marcador anterior removido (si existía)
D/MainActivity: Marcador agregado y ubicación mostrada correctamente
```

### Si hay un error:
```
E/MainActivity: Error en [función]: [mensaje de error]
```

## PUNTOS DE FALLA COMUNES Y CÓMO IDENTIFICARLOS

### 1. Error en onCreate
**Log esperado:** `E/MainActivity: Error en onCreate: [mensaje]`
**Posibles causas:**
- Problema con PreferenceManager
- Error en View Binding
- Layout XML incorrecto

### 2. Error en setupMap
**Log esperado:** `E/MainActivity: Error en setupMap: [mensaje]`
**Posibles causas:**
- OSMDroid no inicializado correctamente
- Problema con MapView en XML
- Permisos faltantes

### 3. Error en searchAddress
**Log esperado:** `E/MainActivity: Error de IO en searchAddress: [mensaje]`
**Posibles causas:**
- Sin conexión a internet
- Geocoder no disponible
- API de Google no configurada

### 4. Error en displayLocation
**Log esperado:** `E/MainActivity: Error en displayLocation: [mensaje]`
**Posibles causas:**
- Coordenadas inválidas
- Problema con marcadores
- MapView no inicializado

## PASOS PARA DEBUGGEAR

1. **Limpia el proyecto:**
   ```
   Build > Clean Project
   Build > Rebuild Project
   ```

2. **Ejecuta la app en modo debug:**
   - Click en el ícono de bug (Shift+F9)
   - O Run > Debug 'app'

3. **Revisa Logcat INMEDIATAMENTE al iniciar**
   - Busca: `D/MainActivity: onCreate iniciado`
   - Si no aparece, hay un problema ANTES de MainActivity

4. **Si la app crashea:**
   - Busca líneas rojas en Logcat
   - Busca: `E/MainActivity:`
   - Lee el stack trace completo

5. **Coloca breakpoints:**
   - Click en el margen izquierdo del código
   - En onCreate (línea ~42)
   - En setupMap (línea ~86)
   - En searchAddress (línea ~142)

## ARCHIVOS IMPORTANTES PARA REVISAR

1. **MainActivity.kt** (modificado)
   - Sistema completo de logging
   - Try-catch en todas las funciones
   - onDestroy() agregado

2. **activity_main.xml**
   - Verifica que todos los IDs coincidan:
     * @+id/editTextAddress
     * @+id/buttonSearch
     * @+id/spinnerMapType
     * @+id/mapView

3. **AndroidManifest.xml**
   - Permisos correctos
   - MainActivity declarada

4. **build.gradle.kts**
   - Dependencia: androidx.preference
   - minSdk = 24

5. **libs.versions.toml**
   - preference = "1.2.1"
   - androidx-preference agregado

## SCRIPT DE COMPILACIÓN

He creado `test_build.bat` en la raíz del proyecto.
Para compilar y ver errores:
1. Doble click en test_build.bat
2. Lee los errores en la consola
3. Busca líneas que digan "error:" o "failed"

## VERIFICACIÓN RÁPIDA

Ejecuta estos comandos en orden:

```bash
# 1. Limpiar builds previos
cd c:\Users\ricoj\AndroidStudioProjects\MAPEO
gradlew.bat clean

# 2. Sincronizar dependencias
gradlew.bat --refresh-dependencies

# 3. Compilar
gradlew.bat assembleDebug

# 4. Ver logs (mientras la app corre)
adb logcat -c && adb logcat | findstr "MainActivity"
```

## PRÓXIMOS PASOS

1. Abre Android Studio
2. Sincroniza el proyecto (Sync Now)
3. Ejecuta la app en modo debug
4. Observa Logcat desde el INICIO
5. Identifica el PRIMER error que aparece
6. Reporta el error específico que ves

## NOTAS IMPORTANTES

- TODOS los errores ahora se registran con detalles
- Los Toast muestran errores al usuario
- Los logs permiten ver la secuencia exacta de ejecución
- El companion object TAG facilita el filtrado

## SI LA APP NO INICIA

Busca en Logcat (sin filtros):
1. `AndroidRuntime: FATAL EXCEPTION`
2. `Process: com.example.mapeo`
3. Lee el stack trace completo
4. El error estará en las primeras líneas después de "Caused by:"
