package com.tuusuario.cyberbattery.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CarbonBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(Color(0xFF0A0A0C))
        val step = 11.dp.toPx()
        val dark = Color(0xFF1A1A1C)
        val mid = Color(0xFF2A2A2E)
        val hi = Color(0xFF3A3A40)
        var x = -size.height
        while (x < size.width + size.height) {
            drawLine(
                color = dark,
                start = Offset(x, 0f),
                end = Offset(x + size.height, size.height),
                strokeWidth = step * 0.55f
            )
            drawLine(
                color = mid,
                start = Offset(x + step * 0.5f, 0f),
                end = Offset(x + step * 0.5f + size.height, size.height),
                strokeWidth = 1.2f
            )
            x += step
        }
        var y = -size.width
        while (y < size.height + size.width) {
            drawLine(
                color = Color(0xFF141416),
                start = Offset(0f, y),
                end = Offset(size.width, y + size.width),
                strokeWidth = step * 0.45f
            )
            drawLine(
                color = hi.copy(alpha = 0.25f),
                start = Offset(0f, y + step * 0.45f),
                end = Offset(size.width, y + step * 0.45f + size.width),
                strokeWidth = 1f
            )
            y += step
        }
    }
}
