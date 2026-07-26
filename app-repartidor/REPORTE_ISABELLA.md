# Reporte de Proyecto: App Repartidor - CafeXpress
**Estudiante:** Isabella Hernández

## 1. Tecnologías Utilizadas
La aplicación ha sido desarrollada bajo una arquitectura moderna de Android, enfocada en la interoperabilidad y el rendimiento:

*   **Kotlin Multiplatform (KMP):** Integración con el módulo `shared` para el manejo de modelos de datos consistentes en todo el ecosistema CafeXpress.
*   **Jetpack Compose:** Framework declarativo para la construcción de una interfaz de usuario fluida y reactiva.
*   **OSMdroid (OpenStreetMap):** Motor de mapas de código abierto para la visualización de rutas y marcadores sin dependencia exclusiva de Google Maps.
*   **Google Play Services Location:** Utilizado para la captura precisa de la ubicación GPS del repartidor en tiempo real.
*   **Kotlinx Serialization:** Manejo eficiente de datos JSON para la recepción de pedidos mediante Intents.
*   **Material Design 3:** Implementación de componentes de diseño modernos (Cards, Surfaces, Snackbars) para una experiencia de usuario estandarizada.

## 2. Funcionalidades de la Aplicación
La App Repartidor cumple el rol crítico de la última milla en el proceso de entrega:

*   **Recepción de Pedidos:** Capacidad de recibir información detallada del pedido (cliente, local, ítems) desde la App Comercio a través de `Intents` y acciones personalizadas.
*   **Seguimiento GPS en Tiempo Real:** Rastreo continuo de la posición del repartidor para actualizar su ubicación en el mapa.
*   **Visualización de Ruta:** Trazado de polilíneas dinámicas que muestran el camino desde la ubicación actual hacia el punto de recogida y posteriormente hacia el destino final.
*   **Interfaz Flotante Optimizada:** Panel de detalles del pedido diseñado para no ser obstruido por los controles del mapa, permitiendo al repartidor ver la información crítica en todo momento.
*   **Validación de Entrega por Proximidad:** Sistema de seguridad que solo permite confirmar la entrega si el repartidor se encuentra dentro de un radio de 100 metros del punto de destino, evitando confirmaciones erróneas.

## 3. Dificultades Encontradas y Soluciones
Durante el desarrollo se enfrentaron retos técnicos significativos:

*   **Errores de Enlace de Recursos (AAPT):** Al inicio, el módulo carecía de recursos básicos de iconos, lo que impedía la compilación. Se solucionó reconstruyendo la estructura de iconos adaptativos mediante vectores para asegurar compatibilidad.
*   **Superposición de Elementos UI:** El mapa inicialmente cubría la información del pedido. Se reestructuró la interfaz usando un `Box` layout de Compose para permitir que la tarjeta de detalles flote sobre el mapa de forma independiente.
*   **Gestión de Ciclo de Vida del Mapa:** La integración de `MapView` (Vista clásica) dentro de Compose requirió el uso de `AndroidView` y `DisposableEffect` para manejar correctamente el pause/resume y evitar fugas de memoria.
*   **Precisión del GPS:** Implementar una lógica de validación que fuera justa para el usuario pero segura. Se ajustó el umbral de distancia y se integró un sistema de `Snackbar` para dar feedback inmediato sobre la distancia actual al destino.

---
*Este reporte documenta el estado actual de la aplicación desarrollada por Isabella Hernández para el examen/proyecto CafeXpress.*
