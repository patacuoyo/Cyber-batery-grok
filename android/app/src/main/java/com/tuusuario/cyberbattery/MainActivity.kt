package com.tuusuario.cyberbattery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuusuario.cyberbattery.ui.screens.DashboardScreen
import com.tuusuario.cyberbattery.ui.theme.CyberBatteryTheme
import com.tuusuario.cyberbattery.ui.theme.Palettes
import com.tuusuario.cyberbattery.viewmodel.BatteryViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BatteryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val style by viewModel.appStyle.collectAsStateWithLifecycle()
            val palette = Palettes.getValue(style)
            val bar = palette.background.toArgb()
            SideEffect {
                enableEdgeToEdge(
                    statusBarStyle = if (palette.light) {
                        SystemBarStyle.light(bar, bar)
                    } else {
                        SystemBarStyle.dark(bar)
                    }
                )
            }
            CyberBatteryTheme(style = style) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = palette.background
                ) {
                    DashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}
