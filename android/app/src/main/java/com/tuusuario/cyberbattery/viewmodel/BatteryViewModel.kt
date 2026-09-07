package com.tuusuario.cyberbattery.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.cyberbattery.data.model.BatteryState
import com.tuusuario.cyberbattery.data.receiver.BatteryReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BatteryViewModel(application: Application) : AndroidViewModel(application) {

    private val _batteryState = MutableStateFlow(BatteryState.Empty)
    val batteryState: StateFlow<BatteryState> = _batteryState.asStateFlow()

    private var batteryReceiver: BatteryReceiver? = null
    private var isRegistered = false

    init {
        registerReceiver()
    }

    private fun registerReceiver() {
        if (isRegistered) return
        val context = getApplication<Application>().applicationContext
        batteryReceiver = BatteryReceiver { state ->
            viewModelScope.launch {
                _batteryState.value = state
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                batteryReceiver,
                BatteryReceiver.createIntentFilter(),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            context.registerReceiver(
                batteryReceiver,
                BatteryReceiver.createIntentFilter()
            )
        }
        isRegistered = true
    }

    private fun unregisterReceiver() {
        if (!isRegistered) return
        val context = getApplication<Application>().applicationContext
        try {
            context.unregisterReceiver(batteryReceiver)
        } catch (_: IllegalArgumentException) {
        }
        isRegistered = false
        batteryReceiver = null
    }

    override fun onCleared() {
        super.onCleared()
        unregisterReceiver()
    }
}
