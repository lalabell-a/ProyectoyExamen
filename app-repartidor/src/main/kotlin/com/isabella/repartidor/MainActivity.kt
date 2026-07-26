package com.isabella.repartidor

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import kotlinx.serialization.json.Json
import org.example.project.shared.model.*
import com.isabella.repartidor.logic.LocationHandler
import com.isabella.repartidor.ui.RepartidorScreen
import org.osmdroid.config.Configuration
import java.io.File

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.location.Location
import androidx.compose.runtime.remember

class MainActivity : ComponentActivity() {

    private lateinit var locationHandler: LocationHandler
    private val jsonHandler = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            locationHandler.startLocationUpdates()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configuración de OSM
        val osmConfig = Configuration.getInstance()
        osmConfig.load(applicationContext, getSharedPreferences("osmdroid_repartidor", Context.MODE_PRIVATE))
        osmConfig.userAgentValue = "Isabella_Repartidor_App"
        
        val baseDir = File(filesDir, "osm_repartidor")
        if (!baseDir.exists()) baseDir.mkdirs()
        osmConfig.osmdroidBasePath = baseDir
        osmConfig.osmdroidTileCache = File(baseDir, "tiles")

        locationHandler = LocationHandler(this)
        
        checkPermissions()

        // Captura de datos del pedido
        val pedidoCargado = handleIntent(intent) ?: createMockPedido()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val location by locationHandler.currentLocation.collectAsState()
                    val snackbarHostState = remember { SnackbarHostState() }
                    
                    RepartidorScreen(
                        pedido = pedidoCargado,
                        currentLocation = location,
                        snackbarHostState = snackbarHostState,
                        onConfirmDelivery = {
                            validarEntrega(location, pedidoCargado, snackbarHostState)
                        }
                    )
                }
            }
        }
    }

    private fun validarEntrega(current: Location?, pedido: PedidoPreparado, snackbar: SnackbarHostState) {
        if (current == null) {
            lifecycleScope.launch { snackbar.showSnackbar("Esperando señal GPS...") }
            return
        }

        val dest = Location("dest").apply {
            latitude = pedido.pedidoOriginal.entrega.lat
            longitude = pedido.pedidoOriginal.entrega.lng
        }

        val distance = current.distanceTo(dest)
        
        lifecycleScope.launch {
            if (distance < 100) { // Umbral de 100 metros para facilitar pruebas
                snackbar.showSnackbar("¡Entrega Confirmada! Distancia: ${distance.toInt()}m")
            } else {
                snackbar.showSnackbar("Error: Estás demasiado lejos del punto (${distance.toInt()}m)")
            }
        }
    }

    private fun handleIntent(intent: Intent?): PedidoPreparado? {
        if (intent == null) return null
        
        // Caso 1: Acción personalizada
        val jsonCustom = intent.getStringExtra("pedido_completo")
        if (jsonCustom != null) return tryParse(jsonCustom)
        
        // Caso 2: ACTION_SEND (Compatibilidad con el Chooser actual de App Comercio)
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val jsonSend = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (jsonSend != null) return tryParse(jsonSend)
        }
        
        return null
    }

    private fun tryParse(json: String): PedidoPreparado? {
        return try {
            jsonHandler.decodeFromString<PedidoPreparado>(json)
        } catch (e: Exception) {
            Log.e("RepartidorApp", "Error parsing JSON: ${e.message}")
            null
        }
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                locationHandler.startLocationUpdates()
            }
            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                )
            }
        }
    }

    private fun createMockPedido(): PedidoPreparado {
        // Pedido de prueba para modo independiente
        val original = PedidoContrato(
            version = "1.0",
            origen = "com.cafexpress.cafeteria",
            pedidoId = "MOCK-REPARTIDOR-123",
            timestamp = "2026-07-25T20:00:00",
            cliente = Cliente("Isabella Hernandez (Mock)", "0987654321"),
            cafeteria = Cafeteria("local-001", "CafeXpress Central", "Av. Amazonas", -0.180653, -78.467834),
            items = listOf(ItemPedido("Cappuccino", "Grande", emptyList(), 1, 3.5, 3.5)),
            entrega = EntregaInfo(-0.190000, -78.480000, "Calle N34", "Edificio Blanco"),
            distanciaKm = 1.5,
            subtotal = 3.5,
            costoEnvio = 1.5,
            total = 5.0
        )
        return PedidoPreparado(
            pedidoOriginal = original,
            puntoRecogida = PuntoRecogida(-0.182000, -78.470000, "Punto de Carga Optimizado"),
            estadoPreparacion = EstadoPedido.LISTO_PARA_RECOGIDA,
            distanciaLocalClienteKm = 1.5,
            distanciaPuntoRecogidaClienteKm = 1.2,
            observaciones = "MODO PRUEBA - INDEPENDIENTE"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        locationHandler.stopLocationUpdates()
    }
}
