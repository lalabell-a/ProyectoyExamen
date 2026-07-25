# CafeXpress - App Comercio (Compañía/Cafetería) ☕

Esta aplicación es el componente intermedio del ecosistema **CafeXpress**. Su función principal es recibir los pedidos realizados por los clientes, permitir al comercio gestionar su preparación y finalmente despacharlos a un repartidor.

## 🚀 Independencia de la App
La aplicación ha sido diseñada para ser **independiente**. 
- Si se recibe un Intent válido desde la App de Cliente, se cargarán los datos reales.
- Si se abre de forma manual (sin Intent), la aplicación genera un **Pedido de Prueba (Mock)** automáticamente. Esto permite probar la interfaz, el mapa y el flujo de estados sin depender de agentes externos.

---

## 📥 Recepción de Pedidos (Intent Inbound)

La aplicación escucha pedidos entrantes mediante un Intent que contiene una cadena JSON.

*   **Origen:** App de Cliente (Stalin).
*   **Mecanismo:** `intent.getStringExtra("pedido")`.
*   **Formato de datos:** Un objeto JSON que mapea a la clase `PedidoContrato`.
*   **Campos clave:**
    *   `pedidoId`: Identificador único.
    *   `cliente`: Datos de contacto.
    *   `cafeteria`: Ubicación y nombre del local.
    *   `items`: Lista de productos y extras.
    *   `entrega`: Coordenadas y dirección del cliente.

---

## 🔄 Flujo de Trabajo Interno

1.  **Recepción:** El pedido aparece en pantalla con el estado "RECIBIDO".
2.  **Preparación:** El usuario presiona "Aceptar y Empezar Cocina" (Estado: "EN PREPARACION").
3.  **Finalización:** Se marca como "LISTO" (Estado: "LISTO PARA RECOGIDA").
4.  **Cálculo de Ruta:** La app utiliza un `CalculadoraRuta` para determinar un punto óptimo de encuentro/recogida basado en la ubicación del local y el cliente.
5.  **Despacho:** Al presionar "Despachar a Repartidor", se genera el Intent de salida.

---

## 📤 Envío a Repartidor (Intent Outbound)

Una vez que el pedido está listo, se envía a la aplicación de Repartidor (App 3).

*   **Acción del Intent:** `com.cafexpress.REPARTIDOR_UPDATE`
*   **Extra:** `pedido_completo`
*   **Categoría:** `Intent.CATEGORY_DEFAULT`
*   **Formato de datos:** Objeto JSON de la clase `PedidoPreparado`.
*   **Contenido:**
    *   Todo el `PedidoContrato` original.
    *   `puntoRecogida`: Coordenadas calculadas para el encuentro.
    *   `distanciaPuntoRecogidaClienteKm`: Distancia optimizada.
    *   `estadoPreparacion`: Confirmación de que está listo.

---

## 🛠️ Tecnologías Utilizadas
- **Kotlin Multiplatform (KMP):** Lógica compartida de modelos y cálculos.
- **Jetpack Compose:** Interfaz de usuario moderna y reactiva.
- **Google Maps Compose:** Visualización de la ubicación del local y puntos de recogida.
- **Kotlinx Serialization:** Manejo eficiente de JSON para la comunicación entre aplicaciones.
