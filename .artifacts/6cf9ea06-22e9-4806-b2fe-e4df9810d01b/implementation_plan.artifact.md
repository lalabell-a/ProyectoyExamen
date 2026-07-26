# Plan de Implementación: App Repartidor (Isabella Hernández)

Este plan detalla la creación de la aplicación para repartidores como parte del ecosistema CafeXpress. La aplicación recibirá pedidos mediante Intents, rastreará la ubicación del repartidor en tiempo real y mostrará una ruta con dos tramos en un mapa interactivo.

## User Review Required

> [!IMPORTANT]
> Se creará un nuevo módulo llamado `app-repartidor` dentro del proyecto actual para facilitar el uso compartido de los modelos de datos en `shared`. Esto permite que la aplicación sea independiente pero coherente con el contrato de datos grupal.

> [!WARNING]
> Para el rastreo de GPS en tiempo real, se requiere que el dispositivo tenga habilitados los servicios de ubicación y que el usuario conceda los permisos necesarios (`ACCESS_FINE_LOCATION`).

## Proposed Changes

### [Gradle & Configuración]

#### [MODIFY] [libs.versions.toml](file:///C:/Users/ionly/AndroidStudioProjects/ProyectoyExamen/gradle/libs.versions.toml)
- Agregar dependencia `play-services-location` para el rastreo GPS.

#### [MODIFY] [settings.gradle.kts](file:///C:/Users/ionly/AndroidStudioProjects/ProyectoyExamen/settings.gradle.kts)
- Incluir el nuevo módulo `:app-repartidor`.

### [Nuevo Módulo: app-repartidor]

#### [NEW] [build.gradle.kts](file:///C:/Users/ionly/AndroidStudioProjects/ProyectoyExamen/app-repartidor/build.gradle.kts)
- Configurar como aplicación Android independiente.
- Incluir dependencias: `:shared`, `osmdroid`, `play-services-location`, `kotlinx-serialization`.

#### [NEW] [AndroidManifest.xml](file:///C:/Users/ionly/AndroidStudioProjects/ProyectoyExamen/app-repartidor/src/main/AndroidManifest.xml)
- Declarar permisos de ubicación e internet.
- Configurar Intent Filter para la acción `com.cafexpress.REPARTIDOR_UPDATE`.

#### [NEW] [MainActivity.kt](file:///C:/Users/ionly/AndroidStudioProjects/ProyectoyExamen/app-repartidor/src/main/kotlin/com/isabella/repartidor/MainActivity.kt)
- Punto de entrada que recibe el Intent y gestiona permisos de ubicación.
- Inicializa el `LocationHandler`.

#### [NEW] [RepartidorScreen.kt](file:///C:/Users/ionly/AndroidStudioProjects/ProyectoyExamen/app-repartidor/src/main/kotlin/com/isabella/repartidor/ui/RepartidorScreen.kt)
- Interfaz de usuario en Compose que muestra:
    - Estado del pedido.
    - Información del Cliente y Local.
    - Mapa interactivo con la ruta en tiempo real.

#### [NEW] [LocationHandler.kt](file:///C:/Users/ionly/AndroidStudioProjects/ProyectoyExamen/app-repartidor/src/main/kotlin/com/isabella/repartidor/logic/LocationHandler.kt)
- Lógica para obtener actualizaciones de ubicación usando `FusedLocationProviderClient`.

## Lógica del Mapa (Requisito Core de Examen)

El mapa (OSMDroid) mostrará:
1.  **Marcador Repartidor**: Se mueve en tiempo real.
2.  **Marcador Local**: Punto de recogida recibido en el Intent.
3.  **Marcador Cliente**: Punto de entrega final.
4.  **Polyline Tramo A**: Repartidor -> Local (color Azul).
5.  **Polyline Tramo B**: Local -> Cliente (color Rojo/Verde).

## Verification Plan

### Automated Tests
- No se requieren pruebas automatizadas complejas para esta fase, pero se verificará la compilación del nuevo módulo.

### Manual Verification
1.  **Prueba de Independencia**: Abrir la app directamente; debe cargar un pedido mock y mostrar la ubicación actual del repartidor en el mapa.
2.  **Prueba de Integración**: Simular el envío de un Intent desde la App Comercio (o mediante ADB) y verificar que la App Repartidor captura los datos correctamente.
3.  **Prueba de Ruta**: Moverse físicamente o usar un "Mock Location" para ver cómo la línea "Repartidor -> Local" se actualiza dinámicamente.
