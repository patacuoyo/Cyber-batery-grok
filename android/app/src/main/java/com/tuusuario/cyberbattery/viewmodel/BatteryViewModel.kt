package com.tuusuario.cyberbattery.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.cyberbattery.data.model.BatteryState
import com.tuusuario.cyberbattery.data.reader.BatteryReader
import com.tuusuario.cyberbattery.data.receiver.BatteryReceiver
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class BatteryViewModel(application: Application) : AndroidViewModel(application) {

    private val _batteryState = MutableStateFlow(BatteryState.Empty)
    val batteryState: StateFlow<BatteryState> = _batteryState.asStateFlow()

    private var batteryReceiver: BatteryReceiver? = null
    private var isRegistered = false
    private var pollJob: Job? = null

    init {
        publish(BatteryReader.read(application))
        registerReceiver()
        startPolling()
    }

    private fun publish(state: BatteryState) {
        _batteryState.value = state
    }

    /**
     * La corriente no llega en el broadcast: se consulta al hardware
     * cada 1,5 s (ni saturado ni lento).
     */
    private fun startPolling() {
        if (pollJob != null) return
        val context = getApplication<Application>().applicationContext
        pollJob = viewModelScope.launch {
            while (isActive) {
                publish(BatteryReader.read(context))
                delay(1_500L)
            }
        }
    }

    private fun registerReceiver() {
        if (isRegistered) return
        val context = getApplication<Application>().applicationContext
        batteryReceiver = BatteryReceiver { state -> publish(state) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                batteryReceiver,
                BatteryReceiver.createIntentFilter(),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            @Suppress("DEPRECATION")
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
        pollJob?.cancel()
        pollJob = null
        unregisterReceiver()
        super.onCleared()
    }
}
