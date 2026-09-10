package com.tuusuario.cyberbattery.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

@Composable
fun CyberBatteryTheme(
    style: AppStyle = AppStyle.BLACK,
    content: @Composable () -> Unit
) {
    val palette = Palettes.getValue(style)
    val scheme = if (palette.light) {
        lightColorScheme(
            primary = palette.accent,
            background = palette.background,
            surface = palette.surface,
            onPrimary = Color.White,
            onBackground = palette.textPrimary,
            onSurface = palette.textPrimary
        )
    } else {
        darkColorScheme(
            primary = palette.accent,
            background = palette.background,
            surface = palette.surface,
            onPrimary = Color.Black,
            onBackground = palette.textPrimary,
            onSurface = palette.textPrimary
        )
    }
    CompositionLocalProvider(LocalStyle provides palette) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}
