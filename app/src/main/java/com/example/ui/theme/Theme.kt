package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EggYellow,
    secondary = EggOrange,
    tertiary = Color(0xFFFFCC80),
    background = CharcoalMidnight,
    surface = EggSurfaceDark,
    onPrimary = CharcoalMidnight,
    onSecondary = CharcoalMidnight,
    onBackground = TextLight,
    onSurface = TextLight,
    surfaceVariant = Color(0xFF2C2C35),
    onSurfaceVariant = Color(0xFFCFD8DC)
)

private val LightColorScheme = lightColorScheme(
    primary = CharcoalMidnight,
    secondary = EggYellow,
    tertiary = EggOrange,
    background = MinimalBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = CharcoalMidnight,
    onBackground = TextDark,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF455A64)
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
