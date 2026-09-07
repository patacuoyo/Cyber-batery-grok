package com.tuusuario.cyberbattery.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.tuusuario.cyberbattery.data.model.BatteryState
import com.tuusuario.cyberbattery.data.reader.BatteryReader

class BatteryReceiver(
    private val onBatteryStateChanged: (BatteryState) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        onBatteryStateChanged(BatteryReader.read(context, intent))
    }

    companion object {
        fun createIntentFilter(): IntentFilter {
            return IntentFilter().apply {
                addAction(Intent.ACTION_BATTERY_CHANGED)
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
            }
        }
    }
}
