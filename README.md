# MAPEO - Buscador de Direcciones con OpenStreetMap

## 📱 Aplicación Android con Kotlin Moderno

Aplicación Android completamente reconstruida desde cero usando **Kotlin moderno**, **Coroutines**, **ViewBinding** y **Material Design 3**.

---

## ✨ Características

- 🗺️ **Búsqueda de direcciones** con Geocoder
- 🎨 **4 tipos de mapas**: Normal, Topográfico, Transporte y Wiki
- 📍 **Marcadores personalizados** con colores según tipo de mapa
- ⚡ **Búsqueda asíncrona** con Kotlin Coroutines
- 🎯 **Material Design 3** con componentes modernos
- 📱 **ViewBinding** para seguridad de tipos
- 🔒 **Manejo de permisos** en tiempo de ejecución
- 📝 **Logging completo** para debugging

---

## 🏗️ Arquitectura

### Stack Tecnológico

- **Lenguaje**: Kotlin 2.0.0
- **SDK**: Android API 24-34 (Android 7.0+)
- **Build**: Gradle 8.5.2 con Kotlin DSL
- **UI**: ViewBinding + Material Design 3
- **Mapas**: OSMDroid 6.1.18
- **Asincronía**: Kotlin Coroutines 1.8.1
- **Permisos**: AndroidX Activity 1.7.0

### Estructura del Proyecto

```
MAPEO/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/mapeo/
│   │   │   │   └── MainActivity.kt          # Activity principal
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   └── activity_main.xml    # Layout con CoordinatorLayout
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml          # Strings localizables
│   │   │   │   │   ├── colors.xml           # Colores de la app
│   │   │   │   │   └── themes.xml           # Tema Material 3
│   │   │   │   └── xml/                     # Configuración XML
│   │   │   └── AndroidManifest.xml          # Manifest con permisos
│   │   └── build.gradle.kts                 # Configuración del módulo
│   └── build.gradle.kts                     # Configuración raíz
├── gradle/
│   └── libs.versions.toml                   # Catálogo de versiones
└── README.md                                # Este archivo
```

---

## 🚀 Instalación y Uso

### Prerrequisitos

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17
- Android SDK API 34
- Gradle 8.5+

### Pasos de Instalación

1. **Clonar o abrir el proyecto** en Android Studio

2. **Sincronizar Gradle**
   ```
   File > Sync Project with Gradle Files
   ```

3. **Limpiar y reconstruir**
   ```
   Build > Clean Project
   Build > Rebuild Project
   ```

4. **Ejecutar en dispositivo o emulador**
   ```
   Run > Run 'app' (Shift+F10)
   ```

---

## 📚 Guía de Uso

### 1. Buscar una Dirección

1. Ingresa una dirección en el campo de texto
2. Presiona el botón "Buscar" o Enter
3. La app buscará la ubicación y mostrará un marcador

### 2. Cambiar Tipo de Mapa

Selecciona uno de los chips:
- **Normal**: Mapa estándar (OpenStreetMap)
- **Topográfico**: Muestra elevaciones y terreno
- **Transporte**: Enfocado en rutas de transporte público
- **Wiki**: Mapa de Wikimedia

### 3. Interactuar con el Mapa

- **Zoom**: Pellizcar o doble tap
- **Desplazar**: Arrastrar con el dedo
- **Ver info**: Tap en el marcador

---

## 🔧 Configuración Técnica

### Dependencias Principales

```kotlin
// Core Android
androidx.core:core-ktx:1.13.1
androidx.appcompat:appcompat:1.7.0
androidx.constraintlayout:constraintlayout:2.1.4
com.google.android.material:material:1.12.0

// Lifecycle
androidx.lifecycle:lifecycle-runtime-ktx:2.8.4
androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1

// Mapas
org.osmdroid:osmdroid-android:6.1.18
androidx.preference:preference-ktx:1.2.1
```

### Permisos Requeridos

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
    android:maxSdkVersion="32" />
```

---

## 🎨 Características de Kotlin Moderno

### 1. Coroutines para Operaciones Asíncronas

```kotlin
lifecycleScope.launch {
    val location = searchAddress(address)  // Suspending function
    withContext(Dispatchers.Main) {
        displayLocation(location, address)
    }
}
```

### 2. ViewBinding Type-Safe

```kotlin
private lateinit var binding: ActivityMainBinding

override fun onCreate(savedInstanceState: Bundle?) {
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    
    binding.searchButton.setOnClickListener { ... }
}
```

### 3. Extension Functions y DSL

```kotlin
mapView = binding.mapView.apply {
    setTileSource(TileSourceFactory.MAPNIK)
    setMultiTouchControls(true)
    controller.apply {
        setZoom(DEFAULT_ZOOM)
        setCenter(GeoPoint(DEFAULT_LAT, DEFAULT_LON))
    }
}
```

### 4. Enum Classes con Propiedades

```kotlin
private enum class MapType(
    val tileSource: ITileSource,
    val markerColor: Int
) {
    NORMAL(TileSourceFactory.MAPNIK, Color.RED),
    TOPOGRAPHIC(TileSourceFactory.OpenTopo, Color.rgb(76, 175, 80))
}
```

### 5. Scope Functions

```kotlin
currentMarker?.let { marker ->
    updateMarkerColor(marker)
}
```

---

## 🐛 Debugging

### Logcat Tags

Filtrar por: `tag:MainActivity`

### Secuencia de Logs Esperada

```
D/MainActivity: onCreate: Iniciando aplicación
D/MainActivity: initializeOSMDroid: Configurando OSMDroid
D/MainActivity: setupMapView: Configurando mapa
D/MainActivity: setupListeners: Configurando listeners
D/MainActivity: onCreate: Inicialización completada exitosamente
```

### Errores Comunes

1. **Error de ViewBinding**
   - Solución: Sincronizar Gradle y limpiar proyecto

2. **Geocoder no disponible**
   - Solución: Verificar conexión a internet

3. **MapView no se muestra**
   - Solución: Verificar permisos de internet

---

## 🔄 Cambios Principales vs Versión Anterior

| Aspecto | Antes | Ahora |
|---------|-------|-------|
| **UI** | XML con Spinner | Material 3 con ChipGroup |
| **Async** | Callbacks anidados | Kotlin Coroutines |
| **Binding** | findViewById | ViewBinding |
| **API Level** | compileSdk 36 | compileSdk 34 |
| **Compose** | Activado sin uso | Eliminado |
| **Arquitectura** | Monolítica | Funciones bien separadas |
| **Logging** | Básico | Completo y estructurado |
| **Geocoder** | Solo deprecated | API 33+ y legacy |

---

## 📖 API Highlights

### MainActivity Methods

```kotlin
// Configuración inicial
private fun initializeOSMDroid()
private fun setupMapView()
private fun setupListeners()

// Búsqueda y visualización
private fun performSearch()
private suspend fun searchAddress(address: String): Address?
private fun displayLocation(address: Address, searchQuery: String)

// Mapas y marcadores
private fun changeMapType(chipId: Int)
private fun updateMarkerColor(marker: Marker)

// UI helpers
private fun showProgress(show: Boolean)
private fun showError(message: String)
private fun showSuccess(message: String)

// Permisos
private fun requestLocationPermissions()
```

---

## 🎯 Mejores Prácticas Implementadas

✅ **Separation of Concerns** - Funciones pequeñas y enfocadas  
✅ **Type Safety** - ViewBinding en lugar de findViewById  
✅ **Null Safety** - Uso de `?.let`, `?:` y null checks  
✅ **Async Programming** - Coroutines en lugar de callbacks  
✅ **Error Handling** - Try-catch y manejo de estados  
✅ **Logging** - Logs estructurados para debugging  
✅ **Resource Management** - Strings externalizadas  
✅ **Lifecycle Awareness** - onResume/onPause del MapView  
✅ **Modern UI** - Material Design 3 components  

---

## 📝 Notas Adicionales

### OSMDroid vs Google Maps

- ✅ **Sin API Key** requerida
- ✅ **Open Source** y gratuito
- ✅ **Offline capabilities** disponibles
- ❌ Menos features que Google Maps
- ❌ Rendimiento ligeramente menor

### Compatibilidad

- **Mínimo**: Android 7.0 (API 24)
- **Target**: Android 14 (API 34)
- **Compilación**: Android 14 (API 34)

---

## 🤝 Contribución

Para reportar bugs o sugerir mejoras:

1. Revisa los logs en Logcat
2. Reproduce el error
3. Documenta los pasos
4. Comparte el stack trace

---

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

---

## 👨‍💻 Desarrollado con

- ❤️ Kotlin
- 🎨 Material Design 3
- 🗺️ OpenStreetMap
- ⚡ Coroutines

---

**Última actualización**: Noviembre 2024  
**Versión**: 1.0.0  
**Estado**: ✅ Producción
