# Walkthrough: App Repartidor - Isabella Hernández

Se ha implementado exitosamente la aplicación para repartidores como un módulo independiente dentro del proyecto **CafeXpress**. Esta aplicación cumple con los requisitos del examen individual (Mapas + GPS) y del proyecto grupal (Interoperabilidad via Intents).

## Cambios Realizados

### 1. Infraestructura y Módulos
- **Nuevo Módulo `:app-repartidor`**: Creado como una aplicación Android nativa que consume la lógica compartida (`shared`).
- **Dependencias**: Se integró `osmdroid` para mapas gratuitos y `play-services-location` para el rastreo GPS de alta precisión.
- **Configuración Gradle**: Actualización de `libs.versions.toml` y `settings.gradle.kts` para soportar el nuevo ecosistema.

### 2. Lógica Core (Examen)
- **Rastreo GPS en Tiempo Real**: Implementado en [LocationHandler.kt](file:///C:/Users/ionly/AndroidStudioProjects/ProyectoyExamen/app-repartidor/src/main/kotlin/com/isabella/repartidor/logic/LocationHandler.kt).
- **Mapa Interactivo**: La pantalla [RepartidorScreen.kt](file:///C:/Users/ionly/AndroidStudioProjects/ProyectoyExamen/app-repartidor/src/main/kotlin/com/isabella/repartidor/ui/RepartidorScreen.kt) utiliza OSMDroid para mostrar:
    - **Marcador Repartidor**: Se mueve dinámicamente según la ubicación real/simulada.
    - **Doble Tramo (Polylines)**:
        - **Azul**: Desde el Repartidor hacia el Local (Punto de Recogida). Se actualiza en tiempo real.
        - **Magenta**: Desde el Local hacia el Cliente (Destino Final).

### 3. Interoperabilidad (Proyecto)
- **Captura de Intents**: La aplicación puede ser lanzada por la App Comercio mediante:
    - El menú de compartir (`ACTION_SEND`).
    - Una acción directa (`com.cafexpress.REPARTIDOR_UPDATE`).
- **Modo Independiente (Mock)**: Si se lanza manualmente, carga datos de prueba para demostrar la funcionalidad sin depender de otras apps.

## Cómo Probar la Aplicación

### Prueba de Independencia (Examen)
1.  En Android Studio, selecciona la configuración de ejecución **app-repartidor**.
2.  Despliega en un emulador o dispositivo físico.
3.  **Verificación**: Verás un pedido de prueba a nombre de "Isabella Hernandez". El mapa mostrará tu ubicación actual y trazará la ruta hacia un local de café ficticio.

### Prueba de Integración (Proyecto)
1.  Lanza la aplicación **androidApp** (Comercio).
2.  Presiona "Aceptar y Empezar Cocina", luego "Marcar como LISTO".
3.  Presiona **"Despachar a Repartidor"**.
4.  En el menú de compartir de Android, selecciona **"CafeXpress Repartidor"**.
5.  **Verificación**: La app de repartidor se abrirá con los datos reales del cliente y local enviados por el comercio.

> [!TIP]
> Para probar el movimiento en el mapa sin caminar, usa la función "Location" de los controles extendidos del emulador de Android para cambiar la latitud/longitud manualmente.

---
**Proyecto listo para ser presentado por Isabella Hernández.**
