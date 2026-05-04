package com.example.gaetdriver.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrangeDark,
    onPrimary = OnPrimaryOrangeDark,
    primaryContainer = PrimaryContainerOrangeDark,
    onPrimaryContainer = OnPrimaryContainerOrangeDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    error = Error
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = OnPrimaryOrange,
    primaryContainer = PrimaryContainerOrange,
    onPrimaryContainer = OnPrimaryContainerOrange,
    background = BackgroundLight,
    surface = SurfaceLight,
    error = Error
)

/**
 * Modern theme implementation focusing on Edge-to-Edge compatibility.
 * Status bar logic is now handled by enableEdgeToEdge in Activity and theme configuration.
 */
@Composable
fun GaetDriverTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
