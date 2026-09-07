package com.tuusuario.cyberbattery.data.model

data class BatteryState(
    val voltage: Float = 0f,
    val current: Int = 0,
    val temperature: Float = 0f,
    val level: Int = 0,
    val watts: Float = 0f,
    val isCharging: Boolean = false
) {
    companion object {
        val Empty = BatteryState()
    }
}
