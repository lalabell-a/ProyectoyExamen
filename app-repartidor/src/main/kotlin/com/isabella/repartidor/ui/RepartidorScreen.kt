package com.isabella.repartidor.ui

import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.example.project.shared.model.PedidoPreparado
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepartidorScreen(
    pedido: PedidoPreparado,
    currentLocation: Location?,
    snackbarHostState: SnackbarHostState,
    onConfirmDelivery: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Repartidor: Isabella H.", color = androidx.compose.ui.graphics.Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color(0xFF6F4E37)) // Espresso
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Mapa como fondo principal
            MapaRepartidor(pedido, currentLocation)

            // Información del Pedido (Flotante Arriba)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFFFFDD0)), // Crema
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pedido: ${pedido.pedidoOriginal.pedidoId}", fontWeight = FontWeight.Bold)
                    Text("Cliente: ${pedido.pedidoOriginal.cliente.nombre}")
                    Text("Destino: ${pedido.pedidoOriginal.entrega.direccion}", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Badge(containerColor = androidx.compose.ui.graphics.Color(0xFF4CAF50)) {
                        Text("ESTADO: ${pedido.estadoPreparacion}", color = androidx.compose.ui.graphics.Color.White)
                    }
                }
            }

            // Botón de acción flotante (Abajo)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f)
            ) {
                Button(
                    onClick = onConfirmDelivery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF6F4E37)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirmar Entrega en Punto", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MapaRepartidor(pedido: PedidoPreparado, currentLocation: Location?) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val pickupPoint = remember { GeoPoint(pedido.puntoRecogida.lat, pedido.puntoRecogida.lng) }
    val deliveryPoint = remember { GeoPoint(pedido.pedidoOriginal.entrega.lat, pedido.pedidoOriginal.entrega.lng) }
    
    // MapView setup
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
            controller.setCenter(pickupPoint)
        }
    }

    // Marcadores
    val markerPickup = remember { 
        Marker(mapView).apply {
            position = pickupPoint
            title = "Punto de Recogida"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
    }
    
    val markerDelivery = remember {
        Marker(mapView).apply {
            position = deliveryPoint
            title = "Cliente: ${pedido.pedidoOriginal.cliente.nombre}"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
    }

    val markerRepartidor = remember {
        Marker(mapView).apply {
            title = "Mi Ubicación (Repartidor)"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        }
    }

    // Polylines
    val polylineRepartidorToLocal = remember {
        Polyline(mapView).apply {
            outlinePaint.color = Color.BLUE
            outlinePaint.strokeWidth = 8f
            title = "Tramo A: Hacia Recogida"
        }
    }

    val polylineLocalToDelivery = remember {
        Polyline(mapView).apply {
            outlinePaint.color = Color.MAGENTA
            outlinePaint.strokeWidth = 8f
            setPoints(listOf(pickupPoint, deliveryPoint))
            title = "Tramo B: Hacia Entrega"
        }
    }

    // Actualizar dinámicamente según GPS
    LaunchedEffect(currentLocation) {
        currentLocation?.let { loc ->
            val pos = GeoPoint(loc.latitude, loc.longitude)
            markerRepartidor.position = pos
            
            // Actualizar Tramo A (Línea recta confiable)
            polylineRepartidorToLocal.setPoints(listOf(pos, pickupPoint))
            
            if (!mapView.overlays.contains(markerRepartidor)) {
                mapView.overlays.add(markerRepartidor)
                mapView.overlays.add(markerPickup)
                mapView.overlays.add(markerDelivery)
                mapView.overlays.add(polylineRepartidorToLocal)
                mapView.overlays.add(polylineLocalToDelivery)
            }
            
            mapView.invalidate()
        }
    }

    DisposableEffect(Unit) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize()
    )
}
