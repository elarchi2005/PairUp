package com.angelcabrera.proyecto.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val PrimaryBlack = Color(0xFF000000)
val SecondaryGray = Color(0xFF757575)
val LightGray = Color(0xFFF5F5F5)
val White = Color(0xFFFFFFFF)
val YellowStar = Color(0xFFFFD700)
val BorderGray = Color(0xFFE0E0E0)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlack,
    secondary = SecondaryGray,
    tertiary = LightGray,
    background = White,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onTertiary = PrimaryBlack,
    onBackground = PrimaryBlack,
    onSurface = PrimaryBlack,
)

@Composable
fun PairUpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}