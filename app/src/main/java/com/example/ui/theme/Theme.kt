package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    secondary = GoldSecondary,
    tertiary = GoldTertiary,
    background = VelvetBlackBackground,
    surface = CharcoalSurface,
    onPrimary = VelvetBlackBackground,
    onSecondary = PureWhite,
    onTertiary = VelvetBlackBackground,
    onBackground = PureWhite,
    onSurface = GoldPrimary,
    error = IncorrectRed
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
