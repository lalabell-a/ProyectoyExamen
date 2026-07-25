package org.example.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.Json
import org.example.project.shared.model.PedidoContrato
import org.example.project.shared.model.PedidoPreparado
import org.example.project.shared.model.*
import org.example.project.ui.theme.CafeXpressTheme
import org.example.project.ui.screens.PedidoScreen
import org.osmdroid.config.Configuration
import java.io.File
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val jsonHandler = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configuración de OpenStreetMap (osmdroid) - SOLUCIÓN PARA EVITAR BLOQUEOS 403
        val osmConfig = Configuration.getInstance()
        // Cargamos una clave de preferencias nueva para asegurar limpieza total
        osmConfig.load(applicationContext, getSharedPreferences("osmdroid_v50_final", Context.MODE_PRIVATE))
        
        // 1. USER-AGENT: Usamos un nombre simple y fijo.
        osmConfig.userAgentValue = "Sebastian_Cafexpress_App_Comercio"
        
        // 2. CACHÉ: Nueva ruta para descartar cualquier tile "403" previo.
        val baseDir = File(filesDir, "osm_v50_final")
        if (!baseDir.exists()) baseDir.mkdirs()
        osmConfig.osmdroidBasePath = baseDir
        osmConfig.osmdroidTileCache = File(baseDir, "tiles")
        
        // 3. TRÁFICO: 2 hilos es el estándar recomendado para no ser bloqueado pero mantener fluidez.
        osmConfig.tileDownloadThreads = 2
        osmConfig.tileFileSystemThreads = 2
        
        // 4. MEJORA VISUAL: Forzamos aceleración por hardware
        osmConfig.isMapViewHardwareAccelerated = true
        
        enableEdgeToEdge()

        // Captura del Intent enviado por App 1 (Stalin)
        val pedidoJson = intent.getStringExtra("pedido")
        val pedidoCargado = try {
            pedidoJson?.let { jsonHandler.decodeFromString<PedidoContrato>(it) }
        } catch (e: Exception) {
            Log.e("CafeXpress", "Error parsing JSON: ${e.message}")
            null
        } ?: createDefaultPedido() // Si no hay intent, crear uno por defecto para independencia

        setContent {
            CafeXpressTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PedidoScreen(
                        pedido = pedidoCargado,
                        onEnviarAApp3 = { pedidoFinal ->
                            enviarAApp3(pedidoFinal)
                        }
                    )
                }
            }
        }
    }

    private fun createDefaultPedido(): PedidoContrato {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val currentTimestamp = sdf.format(Date())
        
        return PedidoContrato(
            version = "1.0",
            origen = "App Comercio (Independiente)",
            pedidoId = "MOCK-${System.currentTimeMillis()}",
            timestamp = currentTimestamp,
            cliente = Cliente(nombre = "Cliente de Prueba", telefono = "0999999999"),
            cafeteria = Cafeteria(
                id = "local-001",
                nombre = "CafeXpress Central",
                direccion = "Av. Principal y 10 de Agosto",
                lat = -0.180653,
                lng = -78.467834
            ),
            items = listOf(
                ItemPedido(producto = "Americano", tamano = "Grande", extras = emptyList<String>(), cantidad = 2, precioUnitario = 2.5, subtotal = 5.0),
                ItemPedido(producto = "Croissant", tamano = "Normal", extras = emptyList<String>(), cantidad = 1, precioUnitario = 1.5, subtotal = 1.5)
            ),
            entrega = EntregaInfo(
                lat = -0.190000,
                lng = -78.480000,
                direccion = "Calle Secundaria N34",
                referencia = "Frente al parque"
            ),
            distanciaKm = 2.5,
            subtotal = 6.5,
            costoEnvio = 1.5,
            total = 8.0
        )
    }

    private fun enviarAApp3(pedido: PedidoPreparado) {
        try {
            val jsonString = jsonHandler.encodeToString(PedidoPreparado.serializer(), pedido)
            
            // Intent para compartir con cualquier app (Chooser)
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, jsonString)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, "Enviar pedido a...")
            startActivity(shareIntent)
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error al enviar el pedido: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun ErrorScreen(mensaje: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = mensaje,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }
}
