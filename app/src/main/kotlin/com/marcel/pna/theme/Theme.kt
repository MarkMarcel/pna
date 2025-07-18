package com.marcel.pna.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val colorScheme = darkColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    error = error,
    onError = onError,
    errorContainer = errorContainer,
    onErrorContainer = onErrorContainer,
    surface = surface,
    onSurface = onSurface,
    background = surface,
    outline = outline,
    outlineVariant = outlineVariant,
    scrim = scrim,
    inverseSurface = inverseSurface,
    inverseOnSurface = inverseOnSurface,
    inversePrimary = inversePrimary,
    surfaceDim = surface,
    surfaceBright = surface,
    surfaceContainerLowest = surface,
    surfaceContainerLow = surface,
    surfaceContainer = surface,
    surfaceContainerHigh = surface,
    surfaceContainerHighest = surface,
)

@Composable
fun PNAMTheme(
    content: @Composable() () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

