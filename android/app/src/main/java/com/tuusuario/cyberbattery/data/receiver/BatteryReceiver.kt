package com.tuusuario.cyberbattery.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.tuusuario.cyberbattery.data.model.BatteryState
import kotlin.math.abs

class BatteryReceiver(
    private val onBatteryStateChanged: (BatteryState) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BATTERY_CHANGED) return

        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = if (level >= 0 && scale > 0) {
            ((level.toFloat() / scale.toFloat()) * 100).toInt()
        } else {
            0
        }

        val voltageMv = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
        val voltage = voltageMv / 1000f

        val tempTenths = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
        val temperature = tempTenths / 10f

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val currentUa = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        } else {
            0
        }
        val currentMa = currentUa / 1000
        val currentAmps = currentMa / 1000f
        val watts = abs(voltage * currentAmps)

        onBatteryStateChanged(
            BatteryState(
                voltage = voltage,
                current = currentMa,
                temperature = temperature,
                level = batteryPct,
                watts = watts,
                isCharging = isCharging
            )
        )
    }

    companion object {
        fun createIntentFilter(): IntentFilter {
            return IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        }
    }
}
