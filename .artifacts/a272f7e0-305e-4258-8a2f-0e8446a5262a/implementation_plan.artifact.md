# Implementation Plan - Road-Following Routing for `app-repartidor`

The user requested that the routes displayed on the map follow actual roads and streets instead of being straight lines. We will integrate the Open Source Routing Machine (OSRM) API to fetch real road geometries.

## Proposed Changes

### Logic & Routing (`:app-repartidor`)

We will create a helper to handle API calls to OSRM and parse the resulting GeoJSON coordinates into `GeoPoint` objects for OSMdroid.

#### [NEW] [RoutingLogic.kt](file:///C:/Users/ionly/AndroidStudioProjects/ProyectoyExamen/app-repartidor/src/main/kotlin/com/isabella/repartidor/logic/RoutingLogic.kt)
- Define data classes for OSRM API response parsing using `kotlinx.serialization`.
- Implement `fetchRoutePoints(start: GeoPoint, end: GeoPoint): List<GeoPoint>` using `java.net.URL` and `Dispatchers.IO`.

### UI & Map Integration (`:app-repartidor`)

We will update the map screen to handle asynchronous route fetching and display the detailed polylines.

#### [MODIFY] [RepartidorScreen.kt](file:///C:/Users/ionly/AndroidStudioProjects/ProyectoyExamen/app-repartidor/src/main/kotlin/com/isabella/repartidor/ui/RepartidorScreen.kt)
- Add state variables (`remember`) for `tramoAPoints` and `tramoBPoints`.
- Implement `LaunchedEffect` to fetch "Tramo B" (Pickup to Delivery) once when the screen starts.
- Update the `LaunchedEffect(currentLocation)` to fetch "Tramo A" (Repartidor to Pickup) dynamically.
- (Optimization): Throttle "Tramo A" updates so we don't spam the public OSRM API on every minor GPS movement.
- Update `Polyline.setPoints()` with the road-following coordinates.

## Verification Plan

### Automated Tests
- Build and run `:app-repartidor:assembleDebug`.

### Manual Verification
- Verify that the blue line (Tramo A) and magenta line (Tramo B) follow the street grid in the map view.
- Ensure that the app remains responsive during the network calls (using `Dispatchers.IO`).
