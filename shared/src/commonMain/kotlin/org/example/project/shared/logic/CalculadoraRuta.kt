package org.example.project.shared.logic

import kotlin.math.*
import org.example.project.shared.model.PuntoRecogida

object CalculadoraRuta {

    private const val RADIO_TIERRA_KM = 6371.0

    /**
     * Calcula la distancia entre dos puntos usando la fórmula de Haversine.
     */
    fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = (lat2 - lat1).toRadians()
        val dLon = (lon2 - lon1).toRadians()
        val a = sin(dLat / 2).pow(2) +
                cos(lat1.toRadians()) * cos(lat2.toRadians()) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return RADIO_TIERRA_KM * c
    }

    /**
     * Calcula el punto óptimo de recogida. 
     * Lógica: Se define como un punto a un 15% de la distancia total desde la cafetería hacia el cliente.
     * Esto simula una "Zona de Carga/Descarga" o "Pickup Point" fuera del local físico.
     */
    fun calcularPuntoOptimo(
        latCafeteria: Double, lngCafeteria: Double,
        latCliente: Double, lngCliente: Double
    ): PuntoRecogida {
        // Interpolación lineal simple para propósitos académicos (válida para distancias cortas urbanas)
        val factor = 0.15 
        val latOptima = latCafeteria + (latCliente - latCafeteria) * factor
        val lngOptima = lngCafeteria + (lngCliente - lngCafeteria) * factor

        return PuntoRecogida(
            lat = latOptima,
            lng = lngOptima,
            descripcion = "Punto de recogida optimizado (Zona de Carga rápida a ${ (factor * 100).toInt() }% del trayecto)"
        )
    }

    private fun Double.toRadians() = this * PI / 180.0
}
