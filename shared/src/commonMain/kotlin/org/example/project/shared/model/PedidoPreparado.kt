package org.example.project.shared.model

import kotlinx.serialization.Serializable

@Serializable
enum class EstadoPedido {
    RECIBIDO,
    EN_PREPARACION,
    LISTO_PARA_RECOGIDA,
    ENTREGADO_A_REPARTIDOR
}

@Serializable
data class PuntoRecogida(
    val lat: Double,
    val lng: Double,
    val descripcion: String
)

@Serializable
data class PedidoPreparado(
    val version: String = "1.0",
    val origen: String = "com.cafexpress.cafeteria",
    val pedidoOriginal: PedidoContrato,
    val puntoRecogida: PuntoRecogida,
    val estadoPreparacion: EstadoPedido,
    val distanciaLocalClienteKm: Double,
    val distanciaPuntoRecogidaClienteKm: Double,
    val observaciones: String
)
