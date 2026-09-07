package com.tuusuario.cyberbattery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuusuario.cyberbattery.data.model.BatteryState
import com.tuusuario.cyberbattery.ui.components.AnimatedMeterBar
import com.tuusuario.cyberbattery.ui.theme.DarkBackground
import com.tuusuario.cyberbattery.ui.theme.DarkCard
import com.tuusuario.cyberbattery.ui.theme.MetallicBorder
import com.tuusuario.cyberbattery.ui.theme.NeonCyan
import com.tuusuario.cyberbattery.ui.theme.NeonGreen
import com.tuusuario.cyberbattery.ui.theme.NeonOrange
import com.tuusuario.cyberbattery.ui.theme.NeonPink
import com.tuusuario.cyberbattery.ui.theme.NeonPurple
import com.tuusuario.cyberbattery.ui.theme.NeonRed
import com.tuusuario.cyberbattery.ui.theme.NeonYellow
import com.tuusuario.cyberbattery.ui.theme.TextPrimary
import com.tuusuario.cyberbattery.ui.theme.TextSecondary
import com.tuusuario.cyberbattery.viewmodel.BatteryViewModel
import kotlin.math.abs

@Composable
fun DashboardScreen(viewModel: BatteryViewModel) {
    val state by viewModel.batteryState.collectAsStateWithLifecycle()
    DashboardContent(state = state)
}

@Composable
fun DashboardContent(state: BatteryState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        HeaderSection(isCharging = state.isCharging)
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkCard)
                .border(1.dp, MetallicBorder, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                AnimatedMeterBar(
                    value = state.level.toFloat(),
                    maxValue = 100f,
                    label = "Nivel de Batería",
                    unit = "%",
                    baseColor = when {
                        state.level < 15 -> NeonRed
                        state.level < 30 -> NeonOrange
                        else -> NeonCyan
                    },
                    isCritical = state.level < 15
                )
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedMeterBar(
                    value = state.voltage,
                    maxValue = 4.5f,
                    label = "Voltaje",
                    unit = "V",
                    baseColor = NeonGreen,
                    isCritical = state.voltage < 3.3f
                )
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedMeterBar(
                    value = abs(state.current).toFloat(),
                    maxValue = 5000f,
                    label = if (state.isCharging) "Corriente de Carga" else "Corriente de Descarga",
                    unit = "mA",
                    baseColor = if (state.isCharging) NeonPink else NeonPurple
                )
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedMeterBar(
                    value = state.watts,
                    maxValue = 25f,
                    label = "Potencia",
                    unit = "W",
                    baseColor = NeonYellow
                )
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedMeterBar(
                    value = state.temperature,
                    maxValue = 50f,
                    label = "Temperatura",
                    unit = "°C",
                    baseColor = when {
                        state.temperature > 40f -> NeonRed
                        state.temperature > 35f -> NeonOrange
                        else -> NeonCyan
                    },
                    isCritical = state.temperature > 40f
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkCard.copy(alpha = 0.6f))
                .padding(16.dp)
        ) {
            Text(
                text = "DATOS EN VIVO",
                color = TextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "V: ${String.format("%.3f", state.voltage)} V  ·  I: ${state.current} mA  ·  P: ${String.format("%.2f", state.watts)} W\nT: ${String.format("%.1f", state.temperature)} °C  ·  Nivel: ${state.level}%",
                color = TextPrimary.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun HeaderSection(isCharging: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "CYBERBATTERY",
                color = NeonCyan,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )
            Text(
                text = "Monitor de Energía en Tiempo Real",
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (isCharging) NeonGreen.copy(alpha = 0.15f)
                    else NeonRed.copy(alpha = 0.15f)
                )
                .border(
                    1.dp,
                    if (isCharging) NeonGreen.copy(alpha = 0.5f) else NeonRed.copy(alpha = 0.5f),
                    RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isCharging) NeonGreen else NeonRed)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isCharging) "CARGANDO" else "EN USO",
                color = if (isCharging) NeonGreen else NeonRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }
    }
}
