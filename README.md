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

## 📚 Preguntas Teóricas sobre Google Maps API

### 1. ¿Cuáles son las principales clases de la API de Google Maps para Android y qué función cumple cada una?

Las principales clases de la API de Google Maps para Android son:

- **`GoogleMap`**: Clase principal que representa el mapa en sí. Permite controlar todos los aspectos visuales y de interacción del mapa, como el tipo de mapa (normal, satélite, híbrido), zoom, cámara, marcadores, y gestos del usuario.

- **`MapFragment` / `SupportMapFragment`**: Fragmentos que contienen un objeto GoogleMap. Se utilizan para mostrar el mapa en la interfaz de usuario. SupportMapFragment es compatible con versiones anteriores de Android mediante la biblioteca de soporte.

- **`Marker`**: Representa un punto de interés en el mapa con un icono personalizable. Se utiliza para señalar ubicaciones específicas y puede incluir información adicional mediante ventanas de información (InfoWindows).

- **`LatLng`**: Clase que representa coordenadas geográficas (latitud y longitud). Es fundamental para posicionar elementos en el mapa como marcadores, polilíneas, y para centrar la cámara.

- **`CameraPosition`**: Define la posición de la cámara del mapa, incluyendo el punto de destino (LatLng), nivel de zoom, ángulo de inclinación y dirección. Permite crear vistas personalizadas del mapa.

- **`Polyline`**: Representa una línea compuesta por múltiples segmentos en el mapa. Se utiliza para dibujar rutas, caminos o cualquier línea entre puntos.

- **`Polygon`**: Representa una forma cerrada en el mapa con múltiples vértices. Se utiliza para delimitar áreas geográficas como zonas, regiones o propiedades.

- **`Circle`**: Representa un círculo en el mapa definido por un centro (LatLng) y un radio en metros. Útil para mostrar áreas de cobertura o proximidad.

- **`GroundOverlay`**: Permite superponer imágenes sobre el mapa ancladas a coordenadas geográficas específicas. Se utiliza para capas personalizadas como planos de edificios o mapas históricos.

- **`UiSettings`**: Controla la configuración de la interfaz de usuario del mapa, como habilitar/deshabilitar gestos (zoom, rotación, desplazamiento), botones de navegación y controles de zoom.

### 2. ¿Cómo se puede agregar un mapa a una aplicación Android usando la API de Google Maps?

Para agregar un mapa a una aplicación Android con Google Maps API, sigue estos pasos:

**Paso 1: Obtener una API Key**
- Accede a Google Cloud Console
- Crea o selecciona un proyecto
- Habilita "Maps SDK for Android"
- Genera una API Key en la sección de credenciales

**Paso 2: Configurar el proyecto**

En `build.gradle.kts` (nivel app):
```kotlin
dependencies {
    implementation("com.google.android.gms:play-services-maps:18.2.0")
}
```

En `AndroidManifest.xml`:
```xml
<manifest>
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    
    <application>
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="TU_API_KEY_AQUI"/>
    </application>
</manifest>
```

**Paso 3: Agregar el fragmento del mapa en el layout**

En `activity_main.xml`:
```xml
<fragment
    android:id="@+id/map"
    android:name="com.google.android.gms.maps.SupportMapFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

**Paso 4: Inicializar el mapa en la Activity**

En `MainActivity.kt`:
```kotlin
class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        
        // Agregar un marcador y mover la cámara
        val location = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(location).title("Marcador"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
    }
}
```

### 3. ¿Qué opciones ofrece la API de Google Maps para mostrar la ubicación del usuario en un mapa?

La API de Google Maps ofrece múltiples opciones para mostrar la ubicación del usuario:

**1. My Location Layer (Capa de Mi Ubicación)**
```kotlin
if (ActivityCompat.checkSelfPermission(this, 
    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
    mMap.isMyLocationEnabled = true
    mMap.uiSettings.isMyLocationButtonEnabled = true
}
```
- Muestra un punto azul en la ubicación actual del usuario
- Incluye un botón para centrar el mapa en la ubicación
- Actualización automática cuando el usuario se mueve

**2. FusedLocationProviderClient**
```kotlin
private val fusedLocationClient = LocationServices
    .getFusedLocationProviderClient(this)

fusedLocationClient.lastLocation.addOnSuccessListener { location ->
    if (location != null) {
        val userLatLng = LatLng(location.latitude, location.longitude)
        mMap.addMarker(MarkerOptions().position(userLatLng).title("Estás aquí"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
    }
}
```
- Obtiene la ubicación más precisa del dispositivo
- Combina GPS, WiFi y redes móviles
- Mayor precisión y eficiencia energética

**3. Location Updates (Actualizaciones Continuas)**
```kotlin
private val locationRequest = LocationRequest.create().apply {
    interval = 10000 // 10 segundos
    fastestInterval = 5000
    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
}

private val locationCallback = object : LocationCallback() {
    override fun onLocationResult(locationResult: LocationResult) {
        locationResult.lastLocation?.let { location ->
            val currentLatLng = LatLng(location.latitude, location.longitude)
            mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng))
        }
    }
}

fusedLocationClient.requestLocationUpdates(locationRequest, 
    locationCallback, Looper.getMainLooper())
```
- Recibe actualizaciones periódicas de la ubicación
- Configurable: intervalo, precisión, prioridad
- Ideal para navegación en tiempo real

**4. Configuración de UI Settings**
```kotlin
mMap.uiSettings.apply {
    isZoomControlsEnabled = true
    isCompassEnabled = true
    isMyLocationButtonEnabled = true
    isMapToolbarEnabled = true
}
```
- Controla la visibilidad de controles de ubicación
- Personaliza la experiencia del usuario

**5. CameraPosition para seguimiento**
```kotlin
val cameraPosition = CameraPosition.Builder()
    .target(userLocation)
    .zoom(17f)
    .bearing(90f) // Orientación
    .tilt(45f)    // Ángulo de visión
    .build()
    
mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
```
- Vista 3D de la ubicación del usuario
- Animaciones fluidas
- Perspectiva personalizable

---

## 💭 Reflexión Personal del Tema

El desarrollo de aplicaciones con mapas representa uno de los aspectos más interesantes y desafiantes de la programación móvil moderna. A través de este proyecto, he comprendido que la integración de servicios de mapas no es simplemente mostrar un mapa en pantalla, sino crear una experiencia de usuario intuitiva y funcional que permita la interacción natural con datos geográficos.

La evolución de OpenStreetMap a Google Maps API demuestra cómo diferentes soluciones tecnológicas pueden adaptarse a distintas necesidades: mientras OSM ofrece libertad y ausencia de costos, Google Maps proporciona mayor precisión y características avanzadas. Esta decisión arquitectónica enseña la importancia de evaluar trade-offs entre recursos, funcionalidad y objetivos del proyecto.

El uso de Kotlin Coroutines para operaciones asíncronas ha sido revelador, mostrando cómo el lenguaje moderno facilita código más limpio y mantenible comparado con callbacks tradicionales. La implementación de ViewBinding y Material Design 3 refuerza la importancia de seguir las mejores prácticas actuales para crear aplicaciones robustas y escalables.

Finalmente, este proyecto refuerza que el desarrollo móvil exitoso requiere no solo conocimientos técnicos sino también comprensión de patrones de diseño, manejo de permisos, gestión de recursos del sistema y experiencia de usuario, aspectos fundamentales para cualquier aplicación profesional moderna.

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
