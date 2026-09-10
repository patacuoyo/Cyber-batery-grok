package com.tuusuario.cyberbattery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuusuario.cyberbattery.data.model.BatteryState
import com.tuusuario.cyberbattery.ui.components.AnimatedMeterBar
import com.tuusuario.cyberbattery.ui.components.CarbonBackground
import com.tuusuario.cyberbattery.ui.theme.AppStyle
import com.tuusuario.cyberbattery.ui.theme.LocalStyle
import com.tuusuario.cyberbattery.ui.theme.Palettes
import com.tuusuario.cyberbattery.ui.theme.StatusColor
import com.tuusuario.cyberbattery.viewmodel.BatteryViewModel
import kotlin.math.abs

@Composable
fun DashboardScreen(viewModel: BatteryViewModel) {
    val state by viewModel.batteryState.collectAsStateWithLifecycle()
    val style by viewModel.appStyle.collectAsStateWithLifecycle()
    DashboardContent(
        state = state,
        style = style,
        onStyle = viewModel::setStyle
    )
}

@Composable
fun DashboardContent(
    state: BatteryState,
    style: AppStyle,
    onStyle: (AppStyle) -> Unit
) {
    val palette = LocalStyle.current
    val light = palette.light
    val chargingNow = state.isCharging || state.current > 0

    Box(modifier = Modifier.fillMaxSize()) {
        if (palette.carbon) {
            CarbonBackground()
        } else {
            Box(Modifier.fillMaxSize().background(palette.background))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            HeaderSection(isCharging = state.isCharging)
            Spacer(modifier = Modifier.height(16.dp))
            StylePicker(selected = style, onStyle = onStyle)
            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.card)
                    .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column {
                    AnimatedMeterBar(
                        value = state.level.toFloat(),
                        maxValue = 100f,
                        label = "Nivel de Batería",
                        unit = "%",
                        barColor = StatusColor.level(state.level, light)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AnimatedMeterBar(
                        value = state.voltage.coerceAtLeast(0f),
                        maxValue = 5.0f,
                        label = "Voltaje",
                        unit = "V",
                        barColor = StatusColor.voltage(state.voltage, light)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AnimatedMeterBar(
                        value = abs(state.current).toFloat(),
                        maxValue = 6000f,
                        label = if (chargingNow) "Corriente de Carga" else "Corriente de Descarga",
                        unit = "mA",
                        barColor = StatusColor.current(state.current, chargingNow, light),
                        textValue = state.current.toFloat()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AnimatedMeterBar(
                        value = state.watts,
                        maxValue = 40f,
                        label = if (state.isCharging) "Potencia de Carga" else "Potencia de Descarga",
                        unit = "W",
                        barColor = StatusColor.watts(state.watts, state.isCharging, light)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AnimatedMeterBar(
                        value = state.temperature,
                        maxValue = 50f,
                        label = "Temperatura",
                        unit = "°C",
                        barColor = StatusColor.temperature(state.temperature, light)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            FooterSection(state = state)
        }
    }
}

@Composable
private fun StylePicker(selected: AppStyle, onStyle: (AppStyle) -> Unit) {
    val palette = LocalStyle.current
    Column {
        Text(
            text = "ESTILO",
            color = palette.textSecondary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppStyle.entries.forEach { style ->
                val p = Palettes.getValue(style)
                val on = style == selected
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (on) palette.accent.copy(alpha = 0.18f) else palette.card)
                        .border(
                            1.dp,
                            if (on) palette.accent else palette.border,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onStyle(style) }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(p.background)
                            .border(1.dp, p.border, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = style.label.uppercase(),
                        color = if (on) palette.accent else palette.textPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(isCharging: Boolean) {
    val palette = LocalStyle.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "CYBERBATTERY",
                color = palette.accent,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )
            Text(
                text = "Monitor de Energía en Tiempo Real",
                color = palette.textSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        val status = if (isCharging) StatusColor.Ok else palette.accent
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(status.copy(alpha = 0.15f))
                .border(1.dp, status.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(status)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isCharging) "CARGANDO" else "EN USO",
                color = status,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun FooterSection(state: BatteryState) {
    val palette = LocalStyle.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.card)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "DATOS EN VIVO  ·  3 s",
            color = palette.textSecondary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = buildString {
                append("V: ${String.format("%.3f", state.voltage)} V  ·  ")
                append("I: ${state.current} mA  ·  ")
                append("P: ${String.format("%.2f", state.watts)} W\n")
                append("T: ${String.format("%.1f", state.temperature)} °C  ·  ")
                append("Nivel: ${state.level}%")
            },
            color = palette.textPrimary.copy(alpha = 0.85f),
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LegendDot(StatusColor.Ok, "OK")
            LegendDot(StatusColor.Warn, "REGULAR")
            LegendDot(StatusColor.Bad, "MALO")
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    val palette = LocalStyle.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(4.dp))
        Text(label, color = palette.textSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
    }
}
