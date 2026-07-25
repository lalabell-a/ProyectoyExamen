package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.example.project.shared.logic.CalculadoraRuta
import org.example.project.shared.model.*
import org.example.project.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidoScreen(
    pedido: PedidoContrato,
    onEnviarAApp3: (PedidoPreparado) -> Unit
) {
    var estadoActual by remember { mutableStateOf(EstadoPedido.RECIBIDO) }
    
    // Cálculo del punto de recogida optimizado
    val puntoOptimo = remember(pedido) {
        CalculadoraRuta.calcularPuntoOptimo(
            pedido.cafeteria.lat, pedido.cafeteria.lng,
            pedido.entrega.lat, pedido.entrega.lng
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comercio: ${pedido.cafeteria.nombre}", color = Leche) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Espresso)
            )
        },
        containerColor = Crema
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Flujo de estados del pedido
            EstadoStepper(estadoActual)

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    InfoCard(titulo = "Detalle del Pedido", iconoEmoji = "ℹ️") {
                        Text("ID: ${pedido.pedidoId}", fontWeight = FontWeight.Bold, color = Cafe)
                        Text("Cliente: ${pedido.cliente.nombre}", color = Espresso)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Borde)
                        pedido.items.forEach { item ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${item.cantidad}x ${item.producto}", color = Espresso)
                                Text("$${item.subtotal}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    Text("Mapa de Ubicación y Recogida (OSM)", fontWeight = FontWeight.Bold, color = Espresso)
                    Spacer(Modifier.height(8.dp))
                    MapaVista(pedido, puntoOptimo)
                }
            }

            // Panel de control inferior
            Surface(tonalElevation = 4.dp, color = Leche, shadowElevation = 8.dp) {
                Box(Modifier.padding(16.dp).navigationBarsPadding()) {
                    Button(
                        onClick = {
                            when (estadoActual) {
                                EstadoPedido.RECIBIDO -> estadoActual = EstadoPedido.EN_PREPARACION
                                EstadoPedido.EN_PREPARACION -> estadoActual = EstadoPedido.LISTO_PARA_RECOGIDA
                                EstadoPedido.LISTO_PARA_RECOGIDA -> {
                                    val preparado = PedidoPreparado(
                                        pedidoOriginal = pedido,
                                        puntoRecogida = puntoOptimo,
                                        estadoPreparacion = EstadoPedido.LISTO_PARA_RECOGIDA,
                                        distanciaLocalClienteKm = pedido.distanciaKm,
                                        distanciaPuntoRecogidaClienteKm = CalculadoraRuta.calcularDistancia(
                                            puntoOptimo.lat, puntoOptimo.lng,
                                            pedido.entrega.lat, pedido.entrega.lng
                                        ),
                                        observaciones = "Punto de recogida calculado para entrega eficiente."
                                    )
                                    onEnviarAApp3(preparado)
                                }
                                else -> {}
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (estadoActual == EstadoPedido.LISTO_PARA_RECOGIDA) ExitoColor else Cafe
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        val textoAccion = when (estadoActual) {
                            EstadoPedido.RECIBIDO -> "Aceptar y Empezar Cocina"
                            EstadoPedido.EN_PREPARACION -> "Marcar como LISTO"
                            else -> "Despachar a Repartidor"
                        }
                        Text(if (estadoActual == EstadoPedido.LISTO_PARA_RECOGIDA) "📤" else "⏳", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(textoAccion, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
fun MapaVista(pedido: PedidoContrato, puntoOptimo: PuntoRecogida) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val posLocal = remember { GeoPoint(pedido.cafeteria.lat, pedido.cafeteria.lng) }
    val posOptima = remember { GeoPoint(puntoOptimo.lat, puntoOptimo.lng) }

    // El MapView se crea una sola vez
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            isTilesScaledToDpi = true // Mejora la visualización en pantallas HD
            controller.setZoom(16.5)
            controller.setCenter(posLocal)

            // Marcador Cafetería
            val markerLocal = Marker(this)
            markerLocal.position = posLocal
            markerLocal.title = "Cafetería: ${pedido.cafeteria.nombre}"
            markerLocal.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            overlays.add(markerLocal)

            // Marcador Punto Óptimo
            val markerOptimo = Marker(this)
            markerOptimo.position = posOptima
            markerOptimo.title = "Punto de Recogida"
            markerOptimo.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            overlays.add(markerOptimo)
        }
    }

    // Gestionar ciclo de vida para evitar peticiones fantasmas en segundo plano
    DisposableEffect(Unit) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
        }
    }

    Box(Modifier.height(300.dp).fillMaxWidth().border(2.dp, Borde, RoundedCornerShape(12.dp))) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
            update = { /* No centrar aquí para permitir arrastrar */ }
        )
    }
    Text(puntoOptimo.descripcion, fontSize = 11.sp, color = Tostado, modifier = Modifier.padding(top = 4.dp))
}


@Composable
fun InfoCard(titulo: String, iconoEmoji: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Leche),
        border = androidx.compose.foundation.BorderStroke(1.dp, Borde)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(iconoEmoji, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Text(titulo, fontWeight = FontWeight.Bold, color = Cafe)
            }
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun EstadoStepper(estado: EstadoPedido) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp).background(Leche, RoundedCornerShape(8.dp)).padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Step("Recibido", true)
        HorizontalDivider(Modifier.width(20.dp), color = if (estado >= EstadoPedido.EN_PREPARACION) Cafe else Borde)
        Step("Cocina", estado >= EstadoPedido.EN_PREPARACION)
        HorizontalDivider(Modifier.width(20.dp), color = if (estado >= EstadoPedido.LISTO_PARA_RECOGIDA) Cafe else Borde)
        Step("Listo", estado >= EstadoPedido.LISTO_PARA_RECOGIDA)
    }
}

@Composable
fun Step(label: String, activo: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(24.dp).background(if (activo) Cafe else Borde, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            if (activo) Text("✅", fontSize = 12.sp)
        }
        Text(label, fontSize = 10.sp, color = if (activo) Espresso else Tostado)
    }
}
