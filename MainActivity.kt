package com.example.mapeo

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.mapeo.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.util.Locale

/**
 * MainActivity - Aplicación de búsqueda de direcciones con OpenStreetMap
 * Construida con Kotlin moderno, Coroutines y ViewBinding
 */
class MainActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityMainBinding
    
    // MapView
    private lateinit var mapView: MapView
    
    // Marcador actual en el mapa
    private var currentMarker: Marker? = null
    
    // Constantes
    private companion object {
        const val TAG = "MainActivity"
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        const val DEFAULT_ZOOM = 10.0
        const val SEARCH_ZOOM = 16.0
        
        // Coordenadas por defecto (Ciudad de México)
        const val DEFAULT_LAT = 19.4326
        const val DEFAULT_LON = -99.1332
    }
    
    // Tipos de mapa con sus respectivos TileSources y colores
    private enum class MapType(
        val tileSource: org.osmdroid.tileprovider.tilesource.ITileSource,
        val markerColor: Int
    ) {
        NORMAL(TileSourceFactory.MAPNIK, Color.RED),
        TOPOGRAPHIC(TileSourceFactory.OpenTopo, Color.rgb(76, 175, 80)),
        TRANSPORT(TileSourceFactory.PUBLIC_TRANSPORT, Color.rgb(33, 150, 243)),
        WIKI(TileSourceFactory.WIKIMEDIA, Color.rgb(255, 152, 0))
    }
    
    private var currentMapType = MapType.NORMAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "onCreate: Iniciando aplicación")
        
        try {
            // Inicializar ViewBinding
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            // Configurar toolbar
            setSupportActionBar(binding.toolbar)
            
            // Inicializar OSMDroid
            initializeOSMDroid()
            
            // Configurar mapa
            setupMapView()
            
            // Configurar listeners
            setupListeners()
            
            // Solicitar permisos
            requestLocationPermissions()
            
            Log.d(TAG, "onCreate: Inicialización completada exitosamente")
            
        } catch (e: Exception) {
            Log.e(TAG, "onCreate: Error durante la inicialización", e)
            showError("Error al inicializar la aplicación: ${e.message}")
        }
    }
    
    /**
     * Inicializa la configuración de OSMDroid
     */
    private fun initializeOSMDroid() {
        Log.d(TAG, "initializeOSMDroid: Configurando OSMDroid")
        
        try {
            // Configurar OSMDroid
            Configuration.getInstance().apply {
                load(
                    applicationContext,
                    androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
                )
                userAgentValue = BuildConfig.APPLICATION_ID
            }
            
            Log.d(TAG, "initializeOSMDroid: Configuración completada")
            
        } catch (e: Exception) {
            Log.e(TAG, "initializeOSMDroid: Error al configurar OSMDroid", e)
            throw e
        }
    }
    
    /**
     * Configura el MapView inicial
     */
    private fun setupMapView() {
        Log.d(TAG, "setupMapView: Configurando mapa")
        
        try {
            mapView = binding.mapView.apply {
                // Configuración básica
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                
                // Configurar controles de zoom
                controller.apply {
                    setZoom(DEFAULT_ZOOM)
                    setCenter(GeoPoint(DEFAULT_LAT, DEFAULT_LON))
                }
                
                // Habilitar scroll
                isHorizontalMapRepetitionEnabled = false
                isVerticalMapRepetitionEnabled = false
                setScrollableAreaLimitLatitude(
                    MapView.getTileSystem().maxLatitude,
                    MapView.getTileSystem().minLatitude,
                    0
                )
            }
            
            Log.d(TAG, "setupMapView: Mapa configurado correctamente")
            
        } catch (e: Exception) {
            Log.e(TAG, "setupMapView: Error al configurar el mapa", e)
            throw e
        }
    }
    
    /**
     * Configura todos los listeners de la UI
     */
    private fun setupListeners() {
        Log.d(TAG, "setupListeners: Configurando listeners")
        
        // Botón de búsqueda
        binding.searchButton.setOnClickListener {
            Log.d(TAG, "searchButton: Click")
            performSearch()
        }
        
        // Enter en el campo de texto
        binding.addressEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Log.d(TAG, "addressEditText: Enter presionado")
                performSearch()
                true
            } else {
                false
            }
        }
        
        // ChipGroup para cambiar tipo de mapa
        binding.mapTypeChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val checkedId = checkedIds[0]
                Log.d(TAG, "mapTypeChipGroup: Chip seleccionado: $checkedId")
                changeMapType(checkedId)
            }
        }
        
        Log.d(TAG, "setupListeners: Listeners configurados")
    }
    
    /**
     * Cambia el tipo de mapa basado en el chip seleccionado
     */
    private fun changeMapType(chipId: Int) {
        Log.d(TAG, "changeMapType: Cambiando tipo de mapa")
        
        try {
            currentMapType = when (chipId) {
                R.id.chipNormal -> MapType.NORMAL
                R.id.chipTopographic -> MapType.TOPOGRAPHIC
                R.id.chipTransport -> MapType.TRANSPORT
                R.id.chipWiki -> MapType.WIKI
                else -> MapType.NORMAL
            }
            
            // Cambiar el tile source
            mapView.setTileSource(currentMapType.tileSource)
            
            // Actualizar color del marcador si existe
            currentMarker?.let { marker ->
                updateMarkerColor(marker)
            }
            
            Log.d(TAG, "changeMapType: Tipo de mapa cambiado a ${currentMapType.name}")
            
        } catch (e: Exception) {
            Log.e(TAG, "changeMapType: Error al cambiar tipo de mapa", e)
            showError("Error al cambiar el tipo de mapa")
        }
    }
    
    /**
     * Ejecuta la búsqueda de dirección
     */
    private fun performSearch() {
        val address = binding.addressEditText.text?.toString()?.trim()
        
        Log.d(TAG, "performSearch: Dirección ingresada: '$address'")
        
        if (address.isNullOrEmpty()) {
            showError(getString(R.string.enter_address))
            return
        }
        
        // Mostrar progress indicator
        showProgress(true)
        
        // Buscar en una coroutine
        lifecycleScope.launch {
            try {
                val location = searchAddress(address)
                
                withContext(Dispatchers.Main) {
                    showProgress(false)
                    
                    if (location != null) {
                        displayLocation(location, address)
                        showSuccess(getString(R.string.location_found))
                    } else {
                        showError(getString(R.string.location_not_found))
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "performSearch: Error durante la búsqueda", e)
                
                withContext(Dispatchers.Main) {
                    showProgress(false)
                    showError("${getString(R.string.search_error)}: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Busca una dirección usando Geocoder
     * Se ejecuta en un hilo de fondo
     */
    private suspend fun searchAddress(address: String): Address? = withContext(Dispatchers.IO) {
        Log.d(TAG, "searchAddress: Buscando dirección en IO thread")
        
        val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // API 33+: Usar método asíncrono
                Log.d(TAG, "searchAddress: Usando Geocoder API 33+")
                
                // Usamos suspendCancellableCoroutine para convertir el callback en suspend function
                kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocationName(address, 1) { addresses ->
                        if (addresses.isNotEmpty()) {
                            continuation.resume(addresses[0]) {}
                        } else {
                            continuation.resume(null) {}
                        }
                    }
                }
            } else {
                // API anterior: Usar método deprecated pero funcional
                Log.d(TAG, "searchAddress: Usando Geocoder API legacy")
                
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(address, 1)
                
                addresses?.firstOrNull()
            }
            
        } catch (e: IOException) {
            Log.e(TAG, "searchAddress: IOException", e)
            null
        }
    }
    
    /**
     * Muestra una ubicación en el mapa
     */
    private fun displayLocation(address: Address, searchQuery: String) {
        Log.d(TAG, "displayLocation: Mostrando ubicación - Lat: ${address.latitude}, Lon: ${address.longitude}")
        
        try {
            val geoPoint = GeoPoint(address.latitude, address.longitude)
            
            // Remover marcador anterior
            currentMarker?.let {
                mapView.overlays.remove(it)
                Log.d(TAG, "displayLocation: Marcador anterior removido")
            }
            
            // Crear nuevo marcador
            val marker = Marker(mapView).apply {
                position = geoPoint
                title = searchQuery
                snippet = address.getAddressLine(0)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                
                // Establecer color según el tipo de mapa
                icon = ContextCompat.getDrawable(
                    this@MainActivity,
                    org.osmdroid.library.R.drawable.marker_default
                )?.apply {
                    setTint(currentMapType.markerColor)
                }
            }
            
            // Agregar marcador al mapa
            mapView.overlays.add(marker)
            currentMarker = marker
            
            // Animar a la ubicación
            mapView.controller.animateTo(geoPoint, SEARCH_ZOOM, 1000L)
            
            // Mostrar info window
            marker.showInfoWindow()
            
            // Invalidar para redibujar
            mapView.invalidate()
            
            Log.d(TAG, "displayLocation: Marcador agregado y vista actualizada")
            
        } catch (e: Exception) {
            Log.e(TAG, "displayLocation: Error al mostrar ubicación", e)
            showError("Error al mostrar la ubicación en el mapa")
        }
    }
    
    /**
     * Actualiza el color del marcador cuando cambia el tipo de mapa
     */
    private fun updateMarkerColor(marker: Marker) {
        try {
            marker.icon = ContextCompat.getDrawable(
                this,
                org.osmdroid.library.R.drawable.marker_default
            )?.apply {
                setTint(currentMapType.markerColor)
            }
            
            mapView.invalidate()
            
            Log.d(TAG, "updateMarkerColor: Color actualizado")
            
        } catch (e: Exception) {
            Log.e(TAG, "updateMarkerColor: Error al actualizar color", e)
        }
    }
    
    /**
     * Solicita permisos de ubicación
     */
    private fun requestLocationPermissions() {
        Log.d(TAG, "requestLocationPermissions: Verificando permisos")
        
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        val permissionsNeeded = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (permissionsNeeded.isNotEmpty()) {
            Log.d(TAG, "requestLocationPermissions: Solicitando permisos")
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            Log.d(TAG, "requestLocationPermissions: Permisos ya concedidos")
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        Log.d(TAG, "onRequestPermissionsResult: requestCode=$requestCode")
        
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Log.d(TAG, "onRequestPermissionsResult: Permisos concedidos")
                    showSuccess(getString(R.string.permissions_granted))
                } else {
                    Log.w(TAG, "onRequestPermissionsResult: Permisos denegados")
                    showError(getString(R.string.permissions_denied))
                }
            }
        }
    }
    
    /**
     * Muestra u oculta el indicador de progreso
     */
    private fun showProgress(show: Boolean) {
        binding.progressIndicator.visibility = if (show) View.VISIBLE else View.GONE
        binding.searchButton.isEnabled = !show
        binding.addressEditText.isEnabled = !show
    }
    
    /**
     * Muestra un mensaje de error
     */
    private fun showError(message: String) {
        Log.w(TAG, "showError: $message")
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.error))
            .show()
    }
    
    /**
     * Muestra un mensaje de éxito
     */
    private fun showSuccess(message: String) {
        Log.d(TAG, "showSuccess: $message")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    // Ciclo de vida del MapView
    override fun onResume() {
        super.onResume()
        try {
            mapView.onResume()
            Log.d(TAG, "onResume: MapView resumido")
        } catch (e: Exception) {
            Log.e(TAG, "onResume: Error", e)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            mapView.onPause()
            Log.d(TAG, "onPause: MapView pausado")
        } catch (e: Exception) {
            Log.e(TAG, "onPause: Error", e)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Activity destruida")
    }
}
