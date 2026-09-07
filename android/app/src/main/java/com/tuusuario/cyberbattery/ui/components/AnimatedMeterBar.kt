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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuusuario.cyberbattery.ui.theme.DarkCard
import com.tuusuario.cyberbattery.ui.theme.MetallicBorder
import com.tuusuario.cyberbattery.ui.theme.NeonRed
import com.tuusuario.cyberbattery.ui.theme.TextSecondary

@Composable
fun AnimatedMeterBar(
    value: Float,
    maxValue: Float,
    label: String,
    unit: String,
    baseColor: Color,
    isCritical: Boolean = false,
    modifier: Modifier = Modifier
) {
    val safeMax = if (maxValue <= 0f) 1f else maxValue
    val targetProgress = (value / safeMax).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 600),
        label = "meterProgress"
    )
    val barColor = if (isCritical) NeonRed else baseColor
    val displayValue = if (unit == "mA" || unit == "%") {
        value.toInt().toString()
    } else {
        String.format("%.2f", value)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
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
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
        ) {
            val cornerRadius = CornerRadius(9.dp.toPx(), 9.dp.toPx())
            val strokeWidth = 2.dp.toPx()
            drawRoundRect(color = DarkCard, size = size, cornerRadius = cornerRadius)
            drawRoundRect(
                color = MetallicBorder,
                size = size,
                cornerRadius = cornerRadius,
                style = Stroke(width = strokeWidth)
            )
            if (animatedProgress > 0.01f) {
                val fillWidth = (size.width - strokeWidth * 2) * animatedProgress
                val fillHeight = size.height - strokeWidth * 2
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(barColor.copy(alpha = 0.7f), barColor)
                    ),
                    topLeft = Offset(strokeWidth, strokeWidth),
                    size = Size(fillWidth, fillHeight),
                    cornerRadius = CornerRadius(7.dp.toPx(), 7.dp.toPx())
                )
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
