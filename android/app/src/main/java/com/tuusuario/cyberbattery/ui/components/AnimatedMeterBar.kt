package com.tuusuario.cyberbattery.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuusuario.cyberbattery.ui.theme.DarkCard
import com.tuusuario.cyberbattery.ui.theme.MetallicBorder
import com.tuusuario.cyberbattery.ui.theme.NeonCyan
import com.tuusuario.cyberbattery.ui.theme.NeonRed
import com.tuusuario.cyberbattery.ui.theme.TextPrimary
import com.tuusuario.cyberbattery.ui.theme.TextSecondary

/**
 * Barra de medición animada estilo cyberpunk/neón.
 * Usa Canvas + animateFloatAsState para un llenado fluido.
 */
@Composable
fun AnimatedMeterBar(
    value: Float,
    maxValue: Float,
    label: String,
    unit: String,
    baseColor: Color,
    isCritical: Boolean = false,
    modifier: Modifier = Modifier,
    textValue: Float? = null
) {
    val safeMax = if (maxValue <= 0f) 1f else maxValue
    val targetProgress = (value / safeMax).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 600),
        label = "meterProgress"
    )

    val barColor = if (isCritical) NeonRed else baseColor
    val shown = textValue ?: value
    val displayValue = when (unit) {
        "mA", "%" -> shown.toInt().toString()
        "V" -> String.format("%.3f", shown)
        else -> String.format("%.2f", shown)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Etiqueta + valor
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label.uppercase(),
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = displayValue,
                    color = barColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Barra Canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
        ) {
            val cornerRadius = CornerRadius(9.dp.toPx(), 9.dp.toPx())
            val strokeWidth = 2.dp.toPx()

            // Fondo oscuro
            drawRoundRect(
                color = DarkCard,
                size = size,
                cornerRadius = cornerRadius
            )

            // Borde metálico
            drawRoundRect(
                color = MetallicBorder,
                size = size,
                cornerRadius = cornerRadius,
                style = Stroke(width = strokeWidth)
            )

            // Barra de llenado neón con gradiente sutil
            if (animatedProgress > 0.01f) {
                val fillWidth = (size.width - strokeWidth * 2) * animatedProgress
                val fillHeight = size.height - strokeWidth * 2

                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            barColor.copy(alpha = 0.7f),
                            barColor
                        )
                    ),
                    topLeft = Offset(strokeWidth, strokeWidth),
                    size = Size(fillWidth, fillHeight),
                    cornerRadius = CornerRadius(7.dp.toPx(), 7.dp.toPx())
                )

                // Brillo superior (efecto neón)
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.25f),
                    topLeft = Offset(strokeWidth, strokeWidth),
                    size = Size(fillWidth, fillHeight * 0.35f),
                    cornerRadius = CornerRadius(7.dp.toPx(), 7.dp.toPx())
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0F)
@Composable
private fun AnimatedMeterBarPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        AnimatedMeterBar(
            value = 72f,
            maxValue = 100f,
            label = "Nivel de Batería",
            unit = "%",
            baseColor = NeonCyan,
            isCritical = false
        )
        AnimatedMeterBar(
            value = 12f,
            maxValue = 100f,
            label = "Nivel Crítico",
            unit = "%",
            baseColor = NeonCyan,
            isCritical = true
        )
    }
}
