package com.tuusuario.cyberbattery.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.cyberbattery.data.model.BatteryState
import com.tuusuario.cyberbattery.data.reader.BatteryReader
import com.tuusuario.cyberbattery.data.receiver.BatteryReceiver
import com.tuusuario.cyberbattery.ui.theme.AppStyle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class BatteryViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("cyberbattery", Context.MODE_PRIVATE)

    private val _batteryState = MutableStateFlow(BatteryState.Empty)
    val batteryState: StateFlow<BatteryState> = _batteryState.asStateFlow()

    private val _appStyle = MutableStateFlow(loadStyle())
    val appStyle: StateFlow<AppStyle> = _appStyle.asStateFlow()

    private var batteryReceiver: BatteryReceiver? = null
    private var isRegistered = false
    private var pollJob: Job? = null

    init {
        publish(BatteryReader.read(application))
        registerReceiver()
        startPolling()
    }

    fun setStyle(style: AppStyle) {
        _appStyle.value = style
        prefs.edit().putString(KEY_STYLE, style.name).apply()
    }

    private fun loadStyle(): AppStyle {
        val raw = prefs.getString(KEY_STYLE, AppStyle.BLACK.name)
        return runCatching { AppStyle.valueOf(raw ?: AppStyle.BLACK.name) }.getOrDefault(AppStyle.BLACK)
    }

    private fun publish(state: BatteryState) {
        _batteryState.value = state
    }

    private fun startPolling() {
        if (pollJob != null) return
        val context = getApplication<Application>().applicationContext
        pollJob = viewModelScope.launch {
            while (isActive) {
                publish(BatteryReader.read(context))
                delay(3_000L)
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

    companion object {
        private const val KEY_STYLE = "app_style"
    }
}
