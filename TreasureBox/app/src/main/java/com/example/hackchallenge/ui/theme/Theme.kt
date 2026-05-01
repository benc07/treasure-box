package com.example.hackchallenge.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Brown,
    onPrimary = SoftWhite,
    primaryContainer = CreamDeep,
    onPrimaryContainer = BrownDark,
    secondary = Plum,
    onSecondary = SoftWhite,
    tertiary = Sage,
    background = Cream,
    onBackground = Ink,
    surface = SoftWhite,
    onSurface = Ink,
    surfaceVariant = CreamDeep,
    onSurfaceVariant = MutedInk
)

@Composable
fun HackChallengeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
