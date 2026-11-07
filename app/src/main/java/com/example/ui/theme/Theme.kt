package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PalezzaBleuNuit,
    onPrimary = PalezzaBlanc,
    primaryContainer = PalezzaBleuNuitLight,
    onPrimaryContainer = PalezzaBlanc,
    secondary = PalezzaBleuElectrique,
    onSecondary = PalezzaBlanc,
    secondaryContainer = Color(0xFFE0E7FF),
    onSecondaryContainer = PalezzaBleuNuit,
    tertiary = PalezzaBleuAccentLight,
    onTertiary = PalezzaBlanc,
    background = PalezzaBlanc,
    onBackground = PalezzaNoir,
    surface = PalezzaBlanc,
    onSurface = PalezzaNoir,
    surfaceVariant = PalezzaGrisSurface,
    onSurfaceVariant = PalezzaTexteSecondaire,
    outline = PalezzaGrisBordure,
    outlineVariant = Color(0xFFCBD5E1)
)

private val DarkColorScheme = darkColorScheme(
    primary = PalezzaBleuElectrique,
    onPrimary = PalezzaBlanc,
    primaryContainer = PalezzaBleuNuit,
    onPrimaryContainer = PalezzaBlanc,
    secondary = PalezzaBleuAccentLight,
    onSecondary = PalezzaBlanc,
    secondaryContainer = PalezzaBleuNuitLight,
    onSecondaryContainer = PalezzaBlanc,
    tertiary = Color(0xFF60A5FA),
    onTertiary = PalezzaNoir,
    background = PalezzaBleuNuitDark,
    onBackground = Color(0xFFF1F5F9),
    surface = PalezzaBleuNuit,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = PalezzaBleuNuitLight,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF1E3A5F),
    outlineVariant = Color(0xFF2E4B72)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
