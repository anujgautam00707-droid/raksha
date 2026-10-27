package com.example.raksha.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SafetyCrimson,
    onPrimary = Color.White,
    primaryContainer = SafetyCrimsonLight,
    onPrimaryContainer = Color.White,
    secondary = SafetyCrimsonDark,
    onSecondary = Color.White,
    tertiary = SafetyAmber,
    onTertiary = Color.Black,
    tertiaryContainer = SafetyAmber,
    onTertiaryContainer = Color.Black,
    error = SafetyCrimsonDark,
    onError = Color.White,
    errorContainer = SafetyCrimsonLight,
    onErrorContainer = Color.White,
    background = Color(0xFFFFFBFA),
    onBackground = Color(0xFF1A1A1C),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1C),
    surfaceVariant = Color(0xFFF2E0E0),
    onSurfaceVariant = Color(0xFF524344)
)

private val DarkColorScheme = darkColorScheme(
    primary = SafetyCrimsonLight,
    onPrimary = Color.Black,
    primaryContainer = SafetyCrimsonDark,
    onPrimaryContainer = Color.White,
    secondary = SafetyCrimson,
    onSecondary = Color.White,
    tertiary = SafetyAmber,
    onTertiary = Color.Black,
    tertiaryContainer = SafetyAmberDark,
    onTertiaryContainer = SafetyAmber,
    error = SafetyCrimsonLight,
    onError = Color.Black,
    errorContainer = SafetyCrimsonDark,
    onErrorContainer = Color.White,
    background = SafetyCharcoal,
    onBackground = SafetyOnDarkSurface,
    surface = Color(0xFF242426),
    onSurface = SafetyOnDarkSurface,
    surfaceVariant = Color(0xFF332426),
    onSurfaceVariant = Color(0xFFD8C2C2)
)

@Composable
fun RakshaTheme(
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