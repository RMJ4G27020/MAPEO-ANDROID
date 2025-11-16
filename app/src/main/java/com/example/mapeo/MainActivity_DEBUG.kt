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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mapeo.databinding.ActivityMainBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mapView: MapView
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var currentMarker: Marker? = null
    
    companion object {
        private const val TAG = "MainActivity"
    }

    // Tipos de mapa para OSM
    private val mapTypes = listOf(
        "Normal" to TileSourceFactory.MAPNIK,
        "Topográfico" to TileSourceFactory.OpenTopo,
        "Público" to TileSourceFactory.PUBLIC_TRANSPORT,
        "Wiki" to TileSourceFactory.WIKIMEDIA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate iniciado")
        
        try {
            // Configuración de OSMDroid
            Configuration.getInstance().load(this, androidx.preference.PreferenceManager.getDefaultSharedPreferences(this))
            Configuration.getInstance().userAgentValue = packageName
            Log.d(TAG, "Configuración OSMDroid completada")
            
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            Log.d(TAG, "View binding completado")

            // Inicializar MapView
            setupMap()
            Log.d(TAG, "Mapa configurado")

            // Configurar Spinner para tipos de mapa
            setupMapTypeSpinner()
            Log.d(TAG, "Spinner configurado")

            // Configurar botón de búsqueda
            binding.buttonSearch.setOnClickListener {
                Log.d(TAG, "Botón de búsqueda presionado")
                searchAddress()
            }

            // Solicitar permisos de ubicación
            requestLocationPermission()
            
            Log.d(TAG, "onCreate completado exitosamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error en onCreate: ${e.message}", e)
            Toast.makeText(this, "Error al inicializar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupMap() {
        try {
            mapView = binding.mapView
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)
            
            // Configurar ubicación predeterminada (Ciudad de México)
            val defaultLocation = GeoPoint(19.4326, -99.1332)
            mapView.controller.setZoom(10.0)
            mapView.controller.setCenter(defaultLocation)
            
            Log.d(TAG, "MapView configurado correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error en setupMap: ${e.message}", e)
            throw e
        }
    }

    private fun setupMapTypeSpinner() {
        try {
            val mapTypeNames = mapTypes.map { it.first }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mapTypeNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerMapType.adapter = adapter

            binding.spinnerMapType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    Log.d(TAG, "Tipo de mapa seleccionado: ${mapTypes[position].first}")
                    try {
                        mapView.setTileSource(mapTypes[position].second)
                        // Cambiar color del marcador según el tipo de mapa
                        updateMarkerColor(position)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al cambiar tipo de mapa: ${e.message}", e)
                        Toast.makeText(this@MainActivity, "Error al cambiar tipo de mapa", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.d(TAG, "Ningún tipo de mapa seleccionado")
                }
            }
            
            Log.d(TAG, "Spinner configurado correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error en setupMapTypeSpinner: ${e.message}", e)
            throw e
        }
    }

    private fun updateMarkerColor(mapTypePosition: Int) {
        currentMarker?.let { marker ->
            try {
                val geoPoint = marker.position
                val title = marker.title
                val snippet = marker.snippet
                
                mapView.overlays.remove(marker)
                
                // Crear nuevo marcador con color diferente
                val newMarker = Marker(mapView)
                newMarker.position = geoPoint
                newMarker.title = title
                newMarker.snippet = snippet
                newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                
                // Cambiar color según el tipo de mapa
                when (mapTypePosition) {
                    0 -> {
                        // Normal - Rojo (color predeterminado)
                        newMarker.icon = ContextCompat.getDrawable(this@MainActivity, org.osmdroid.library.R.drawable.marker_default)
                    }
                    1 -> {
                        // Topográfico - Verde
                        val icon = ContextCompat.getDrawable(this@MainActivity, org.osmdroid.library.R.drawable.marker_default_focused_base)
                        icon?.setTint(Color.GREEN)
                        newMarker.icon = icon
                    }
                    2 -> {
                        // Público - Azul
                        val icon = ContextCompat.getDrawable(this@MainActivity, org.osmdroid.library.R.drawable.marker_default_focused_base)
                        icon?.setTint(Color.BLUE)
                        newMarker.icon = icon
                    }
                    3 -> {
                        // Wiki - Naranja
                        val icon = ContextCompat.getDrawable(this@MainActivity, org.osmdroid.library.R.drawable.marker_default_focused_base)
                        icon?.setTint(Color.rgb(255, 165, 0))
                        newMarker.icon = icon
                    }
                }
                
                mapView.overlays.add(newMarker)
                currentMarker = newMarker
                mapView.invalidate()
                
                Log.d(TAG, "Color del marcador actualizado para tipo: $mapTypePosition")
            } catch (e: Exception) {
                Log.e(TAG, "Error en updateMarkerColor: ${e.message}", e)
            }
        }
    }

    private fun searchAddress() {
        val addressText = binding.editTextAddress.text.toString().trim()
        
        Log.d(TAG, "Buscando dirección: $addressText")
        
        if (addressText.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa una dirección", Toast.LENGTH_SHORT).show()
            return
        }

        val geocoder = Geocoder(this, Locale.getDefault())
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // API 33+: Usar el nuevo método asíncrono
                Log.d(TAG, "Usando Geocoder API 33+")
                geocoder.getFromLocationName(addressText, 1) { addresses ->
                    runOnUiThread {
                        if (addresses.isEmpty()) {
                            Log.w(TAG, "No se encontró la dirección")
                            Toast.makeText(this, "Dirección no encontrada", Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }
                        Log.d(TAG, "Dirección encontrada: ${addresses[0]}")
                        displayLocation(addresses[0], addressText)
                    }
                }
            } else {
                // API anterior a 33: Usar el método deprecated
                Log.d(TAG, "Usando Geocoder API legacy")
                @Suppress("DEPRECATION")
                val addresses: List<Address>? = geocoder.getFromLocationName(addressText, 1)
                
                if (addresses.isNullOrEmpty()) {
                    Log.w(TAG, "No se encontró la dirección")
                    Toast.makeText(this, "Dirección no encontrada", Toast.LENGTH_SHORT).show()
                    return
                }
                Log.d(TAG, "Dirección encontrada: ${addresses[0]}")
                displayLocation(addresses[0], addressText)
            }
            
        } catch (e: IOException) {
            Log.e(TAG, "Error de IO en searchAddress: ${e.message}", e)
            Toast.makeText(this, "Error al buscar la dirección: ${e.message}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error general en searchAddress: ${e.message}", e)
            Toast.makeText(this, "Error inesperado: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun displayLocation(address: Address, addressText: String) {
        try {
            Log.d(TAG, "Mostrando ubicación: Lat=${address.latitude}, Lng=${address.longitude}")
            
            val geoPoint = GeoPoint(address.latitude, address.longitude)
            
            // Remover marcador anterior si existe
            currentMarker?.let {
                mapView.overlays.remove(it)
                Log.d(TAG, "Marcador anterior removido")
            }
            
            // Crear nuevo marcador
            val marker = Marker(mapView)
            marker.position = geoPoint
            marker.title = addressText
            marker.snippet = address.getAddressLine(0)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            
            // Obtener color según el tipo de mapa seleccionado
            val mapTypePosition = binding.spinnerMapType.selectedItemPosition
            when (mapTypePosition) {
                0 -> {
                    // Normal - Rojo (color predeterminado)
                    marker.icon = ContextCompat.getDrawable(this, org.osmdroid.library.R.drawable.marker_default)
                }
                1 -> {
                    // Topográfico - Verde
                    val icon = ContextCompat.getDrawable(this, org.osmdroid.library.R.drawable.marker_default_focused_base)
                    icon?.setTint(Color.GREEN)
                    marker.icon = icon
                }
                2 -> {
                    // Público - Azul
                    val icon = ContextCompat.getDrawable(this, org.osmdroid.library.R.drawable.marker_default_focused_base)
                    icon?.setTint(Color.BLUE)
                    marker.icon = icon
                }
                3 -> {
                    // Wiki - Naranja
                    val icon = ContextCompat.getDrawable(this, org.osmdroid.library.R.drawable.marker_default_focused_base)
                    icon?.setTint(Color.rgb(255, 165, 0))
                    marker.icon = icon
                }
            }
            
            mapView.overlays.add(marker)
            currentMarker = marker
            
            // Mover cámara a la ubicación
            mapView.controller.animateTo(geoPoint)
            mapView.controller.setZoom(15.0)
            
            // Mostrar info window del marcador
            marker.showInfoWindow()
            
            Toast.makeText(this, "Ubicación encontrada", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Marcador agregado y ubicación mostrada correctamente")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error en displayLocation: ${e.message}", e)
            Toast.makeText(this, "Error al mostrar la ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestLocationPermission() {
        try {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "Solicitando permisos de ubicación")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                Log.d(TAG, "Permisos de ubicación ya concedidos")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en requestLocationPermission: ${e.message}", e)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        Log.d(TAG, "onRequestPermissionsResult: requestCode=$requestCode")
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permisos concedidos")
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show()
            } else {
                Log.w(TAG, "Permisos denegados")
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Ciclo de vida del MapView
    override fun onResume() {
        super.onResume()
        try {
            mapView.onResume()
            Log.d(TAG, "onResume - MapView resumido")
        } catch (e: Exception) {
            Log.e(TAG, "Error en onResume: ${e.message}", e)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            mapView.onPause()
            Log.d(TAG, "onPause - MapView pausado")
        } catch (e: Exception) {
            Log.e(TAG, "Error en onPause: ${e.message}", e)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy llamado")
    }
}
