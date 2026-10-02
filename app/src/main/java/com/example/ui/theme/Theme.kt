package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val SadeeqiColorScheme = darkColorScheme(
    primary = SadeeqiYellow,
    onPrimary = SadeeqiBlack,
    primaryContainer = SadeeqiYellowDark,
    onPrimaryContainer = SadeeqiBlack,
    secondary = SadeeqiGold,
    onSecondary = SadeeqiBlack,
    error = SadeeqiRed,
    onError = SadeeqiWhite,
    background = SadeeqiBlack,
    onBackground = SadeeqiWhite,
    surface = SadeeqiDarkCard,
    onSurface = SadeeqiWhite,
    surfaceVariant = SadeeqiDarkSurface,
    onSurfaceVariant = SadeeqiLightGray,
    outline = SadeeqiDarkBorder
)

@Composable
fun SadeeqiTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = SadeeqiColorScheme,
            typography = Typography,
            content = content
        )
    }
}
