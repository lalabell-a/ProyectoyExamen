package org.example.project.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Espresso = Color(0xFF3E2723)
val Cafe = Color(0xFF6F4E37)
val Caramelo = Color(0xFFD4A373)
val Crema = Color(0xFFFAF3E9)
val Leche = Color(0xFFFFFFFF)
val Tostado = Color(0xFF8D6E63)
val Borde = Color(0xFFE7DCCF)
val ErrorColor = Color(0xFFC62828)
val ExitoColor = Color(0xFF2E7D32)

private val LightColorScheme = lightColorScheme(
    primary = Cafe,
    onPrimary = Leche,
    secondary = Tostado,
    onSecondary = Leche,
    tertiary = Caramelo,
    background = Crema,
    surface = Leche,
    onBackground = Espresso,
    onSurface = Espresso,
    error = ErrorColor,
    outline = Borde
)

@Composable
fun CafeXpressTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
