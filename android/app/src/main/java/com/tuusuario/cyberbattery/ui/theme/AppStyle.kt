package com.tuusuario.cyberbattery.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppStyle(val label: String) {
    BLACK("Negro"),
    WHITE("Blanco"),
    ANTHRACITE("Antracita"),
    CARBON("Fibra carbono")
}

data class StylePalette(
    val background: Color,
    val surface: Color,
    val card: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val track: Color,
    val carbon: Boolean = false,
    val light: Boolean = false
)

object StatusColor {
    val Ok = Color(0xFF2ECC71)
    val Warn = Color(0xFFF1C40F)
    val Bad = Color(0xFFE74C3C)
    val OkDark = Color(0xFF1B8A3A)
    val WarnDark = Color(0xFFC49000)
    val BadDark = Color(0xFFC0392B)

    fun triple(light: Boolean): Triple<Color, Color, Color> =
        if (light) Triple(OkDark, WarnDark, BadDark) else Triple(Ok, Warn, Bad)

    fun of(ok: Boolean, warn: Boolean, light: Boolean): Color {
        val (g, y, r) = triple(light)
        return when {
            ok -> g
            warn -> y
            else -> r
        }
    }

    fun level(pct: Int, light: Boolean) = of(pct >= 50, pct >= 20, light)

    fun voltage(v: Float, light: Boolean) = of(
        v in 3.60f..4.25f,
        v in 3.40f..3.60f || v in 4.25f..4.40f,
        light
    )

    fun current(ma: Int, charging: Boolean, light: Boolean): Color {
        val a = kotlin.math.abs(ma)
        return if (charging) of(a in 150..3500, a in 50..150 || a in 3500..5500, light)
        else of(a in 40..900, a in 900..1800, light)
    }

    fun watts(w: Float, charging: Boolean, light: Boolean): Color {
        return if (charging) of(w in 0.8f..28f, w in 0.2f..0.8f || w in 28f..40f, light)
        else of(w in 0.2f..5f, w in 5f..10f, light)
    }

    fun temperature(c: Float, light: Boolean) = of(c < 35f, c < 40f, light)
}

val Palettes = mapOf(
    AppStyle.BLACK to StylePalette(
        background = Color(0xFF050507),
        surface = Color(0xFF0E0E12),
        card = Color(0xFF16161C),
        border = Color(0xFF2C2C36),
        textPrimary = Color(0xFFE8E8F0),
        textSecondary = Color(0xFF8A8A9A),
        accent = Color(0xFF00E5FF),
        track = Color(0xFF101014)
    ),
    AppStyle.WHITE to StylePalette(
        background = Color(0xFFF3F3F6),
        surface = Color(0xFFFFFFFF),
        card = Color(0xFFFFFFFF),
        border = Color(0xFFC9C9D1),
        textPrimary = Color(0xFF1A1A1E),
        textSecondary = Color(0xFF5C5C66),
        accent = Color(0xFF007A8A),
        track = Color(0xFFE7E7EC),
        light = true
    ),
    AppStyle.ANTHRACITE to StylePalette(
        background = Color(0xFF2A2A2E),
        surface = Color(0xFF323236),
        card = Color(0xFF3A3A40),
        border = Color(0xFF5A5A62),
        textPrimary = Color(0xFFF0F0F2),
        textSecondary = Color(0xFFB0B0B8),
        accent = Color(0xFF7FDBFF),
        track = Color(0xFF26262A)
    ),
    AppStyle.CARBON to StylePalette(
        background = Color(0xFF0B0B0D),
        surface = Color(0xFF121214),
        card = Color(0xCC1A1A1E),
        border = Color(0xFF3A3A40),
        textPrimary = Color(0xFFEDEDEE),
        textSecondary = Color(0xFF9A9AA2),
        accent = Color(0xFFFFC107),
        track = Color(0xFF0E0E10),
        carbon = true
    )
)

val LocalStyle = staticCompositionLocalOf { Palettes.getValue(AppStyle.BLACK) }
