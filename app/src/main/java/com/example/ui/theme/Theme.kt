package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ChefPrimary,
    onPrimary = ChefOnPrimary,
    primaryContainer = ChefPrimaryContainer,
    onPrimaryContainer = ChefOnPrimaryContainer,
    secondary = ChefSecondary,
    onSecondary = ChefOnSecondary,
    secondaryContainer = ChefSecondaryContainer,
    onSecondaryContainer = ChefOnSecondaryContainer,
    tertiary = ChefTertiary,
    onTertiary = ChefOnTertiary,
    tertiaryContainer = ChefTertiaryContainer,
    onTertiaryContainer = ChefOnTertiaryContainer,
    background = ChefBackground,
    onBackground = ChefOnBackground,
    surface = ChefSurface,
    onSurface = ChefOnSurface,
    surfaceVariant = ChefSurfaceVariant,
    onSurfaceVariant = ChefOnSurfaceVariant,
    surfaceContainerLowest = ChefSurfaceContainerLowest,
    surfaceContainerLow = ChefSurfaceContainerLow,
    surfaceContainer = ChefSurfaceContainer,
    surfaceContainerHigh = ChefSurfaceContainerHigh,
    surfaceContainerHighest = ChefSurfaceContainerHighest,
    outline = ChefOutline,
    outlineVariant = ChefOutlineVariant,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB4A1),
    onPrimary = Color(0xFF621000),
    primaryContainer = Color(0xFF881F00),
    onPrimaryContainer = Color(0xFFFFDBD1),
    secondary = Color(0xFF9DD3AA),
    onSecondary = Color(0xFF00391A),
    secondaryContainer = Color(0xFF1E5031),
    onSecondaryContainer = Color(0xFFB6EDC2),
    tertiary = Color(0xFFFFB955),
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = Color(0xFF633F00),
    onTertiaryContainer = Color(0xFFFFDDB4),
    background = Color(0xFF101412),
    onBackground = Color(0xFFE1E5E0),
    surface = Color(0xFF101412),
    onSurface = Color(0xFFE1E5E0),
    surfaceVariant = Color(0xFF534340),
    onSurfaceVariant = Color(0xFFD8C2BD),
    surfaceContainerLowest = Color(0xFF0B0F0D),
    surfaceContainerLow = Color(0xFF191D1A),
    surfaceContainer = Color(0xFF1D211E),
    surfaceContainerHigh = Color(0xFF272B28),
    surfaceContainerHighest = Color(0xFF323633),
    outline = Color(0xFFA08C86),
    outlineVariant = Color(0xFF534340),
)

@Composable
fun ChefMarketTheme(
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
