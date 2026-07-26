# Speech para Exposición: App Repartidor CafeXpress
**Ponente:** Isabella Hernández

---

### 1. Introducción y Propósito (30 segundos)
"Hola a todos. Mi nombre es Isabella Hernández y hoy les presento la **App Repartidor** del ecosistema CafeXpress.

Esta aplicación es el eslabón final de nuestra cadena: se encarga de que el pedido llegue del local a las manos del cliente de forma eficiente. Mi objetivo fue crear una herramienta que no solo muestre una ruta, sino que garantice que la entrega se haga en el lugar correcto."

### 2. Tecnologías y Funcionalidades (1 minuto)
"Para lograrlo, utilicé **Kotlin Multiplatform** para compartir lógica con el resto del proyecto y **Jetpack Compose** para una interfaz moderna.

La app destaca por tres cosas:
1.  **Integración inteligente:** Recibe el pedido directamente desde la App Comercio mediante Intents de Android.
2.  **Navegación Dinámica:** Uso **OSMdroid** para trazar la ruta en tiempo real, dividiéndola en dos tramos: del repartidor al local, y del local al cliente.
3.  **Seguridad en la Entrega:** Implementé un sistema de geovallas. El botón de 'Confirmar Entrega' no es solo un botón; valida mediante el GPS si el repartidor está realmente en el destino (a menos de 100 metros) antes de permitir cerrar el pedido."

### 3. Retos y Soluciones (45 segundos)
"No fue un camino lineal. El mayor reto fue la **UX en movimiento**.
*   **Primero**, tuve problemas con los recursos visuales que bloqueaban la compilación, lo cual solucioné estandarizando iconos vectoriales.
*   **Segundo**, el mapa 'se comía' la información del pedido. Tuve que rediseñar la jerarquía visual usando capas flotantes para que el repartidor nunca pierda de vista los detalles del cliente mientras conduce.
*   **Finalmente**, integrar componentes clásicos de mapas en el mundo declarativo de Compose requirió un manejo preciso del ciclo de vida para evitar que la app se volviera lenta."

### 4. Conclusión (15 segundos)
"En resumen, la App Repartidor combina rastreo GPS preciso con una interfaz limpia y segura, asegurando que la experiencia de CafeXpress termine con un cliente satisfecho y una entrega verificada. Muchas gracias."
