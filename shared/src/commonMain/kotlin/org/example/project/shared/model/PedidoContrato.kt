package org.example.project.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class PedidoContrato(
    val version: String,
    val origen: String,
    val pedidoId: String,
    val timestamp: String,
    val cliente: Cliente,
    val cafeteria: Cafeteria,
    val items: List<ItemPedido>,
    val entrega: EntregaInfo,
    val distanciaKm: Double,
    val subtotal: Double,
    val costoEnvio: Double,
    val total: Double
)

@Serializable
data class Cliente(val nombre: String, val telefono: String)

@Serializable
data class Cafeteria(
    val id: String,
    val nombre: String,
    val direccion: String,
    val lat: Double,
    val lng: Double
)

@Serializable
data class ItemPedido(
    val producto: String,
    val tamano: String,
    val extras: List<String>,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double,
    val notas: String? = null
)

@Serializable
data class EntregaInfo(
    val lat: Double,
    val lng: Double,
    val direccion: String,
    val referencia: String? = null
)
