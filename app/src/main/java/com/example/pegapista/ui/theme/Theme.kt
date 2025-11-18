package com.example.pegapista.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = BluePrimary,
    onPrimary = OnPrimaryLight,

    background = BackgroundLight,
    onBackground = OnBackgroundLight,

    surface = SurfaceLight,
    onSurface = OnBackgroundLight,

    error = ErrorRed,
    onError = OnError
)

private val DarkColors = darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = OnPrimaryDark,

    background = BackgroundDark,
    onBackground = OnBackgroundDark,

    surface = SurfaceDark,
    onSurface = OnBackgroundDark,

    error = ErrorRed,
    onError = OnError
)

@Composable
fun PegaPistaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}
