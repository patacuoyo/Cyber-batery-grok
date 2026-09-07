package com.tuusuario.cyberbattery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.tuusuario.cyberbattery.ui.screens.DashboardScreen
import com.tuusuario.cyberbattery.ui.theme.CyberBatteryTheme
import com.tuusuario.cyberbattery.ui.theme.DarkBackground
import com.tuusuario.cyberbattery.viewmodel.BatteryViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BatteryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CyberBatteryTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = DarkBackground
                ) {
                    DashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}
