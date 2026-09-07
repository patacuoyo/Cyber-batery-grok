package com.tuusuario.cyberbattery.data.reader

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.tuusuario.cyberbattery.data.model.BatteryState
import kotlin.math.abs

/**
 * Lectura unificada de sensores de batería.
 *
 * ACTION_BATTERY_CHANGED NO incluye la corriente y apenas se dispara,
 * por eso voltaje/corriente/potencia hay que releerlos en cada ciclo.
 *
 * Unidades reales según fabricante:
 * - Voltaje extra: mV (oficial), a veces µV o ya en V.
 * - CURRENT_NOW: µA (oficial). Samsung/Xiaomi/OPPO a menudo ya viene en mA.
 * - Long.MIN_VALUE / Int.MIN_VALUE = propiedad no soportada.
 */
object BatteryReader {

    fun read(context: Context, incoming: Intent? = null): BatteryState {
        val app = context.applicationContext
        val batteryManager = app.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val intent = incoming?.takeIf { it.action == Intent.ACTION_BATTERY_CHANGED }
            ?: stickyBatteryIntent(app)

        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = when {
            level >= 0 && scale > 0 -> ((level.toFloat() / scale.toFloat()) * 100f).toInt()
            else -> safeCapacity(batteryManager)
        }

        val voltage = toVolts(intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0)
        val temperature = (intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0) / 10f

        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL ||
            plugged != 0 ||
            batteryManager.isCharging

        val currentMa = readCurrentMilliAmps(batteryManager, isCharging)
        val amps = currentMa / 1000f
        val watts = abs(voltage * amps)

        return BatteryState(
            voltage = voltage,
            current = currentMa,
            temperature = temperature,
            level = batteryPct.coerceIn(0, 100),
            watts = watts,
            isCharging = isCharging
        )
    }

    private fun stickyBatteryIntent(context: Context): Intent? {
        return try {
            context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        } catch (_: Exception) {
            null
        }
    }

    private fun safeCapacity(batteryManager: BatteryManager): Int {
        val cap = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return if (cap in 0..100) cap else 0
    }

    /**
     * Convierte el extra de voltaje a voltios reales.
     * healthd publica mV; algunos kernels/OEM publican µV o V enteros.
     */
    fun toVolts(raw: Int): Float {
        val magnitude = abs(raw)
        return when {
            magnitude >= 1_000_000 -> raw / 1_000_000f // microvoltios
            magnitude >= 1_000 -> raw / 1_000f         // milivoltios
            magnitude in 2..6 -> raw.toFloat()         // ya son voltios
            else -> 0f
        }
    }

    private fun readCurrentMilliAmps(batteryManager: BatteryManager, isCharging: Boolean): Int {
        val nowLong = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        val nowInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        val avgLong = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)
        val avgInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)

        val raw = firstValid(nowLong, nowInt.toLong(), avgLong, avgInt.toLong())
        if (raw == 0L) return 0

        var milliAmps = toMilliAmps(raw)

        // Convención AOSP: positivo = corriente que ENTRA a la batería (carga).
        // Algunos OEM invierten el signo. Si está enchufado y sale negativo, se corrige.
        if (isCharging && milliAmps < 0) {
            milliAmps = -milliAmps
        }
        if (!isCharging && milliAmps > 0) {
            milliAmps = -milliAmps
        }

        return milliAmps.coerceIn(-30_000, 30_000)
    }

    private fun firstValid(vararg values: Long): Long {
        for (value in values) {
            if (isSupported(value) && value != 0L) return value
        }
        return 0L
    }

    private fun isSupported(value: Long): Boolean {
        return value != Long.MIN_VALUE && value != Int.MIN_VALUE.toLong()
    }

    /**
     * |valor| >= 10_000 → µA (estándar AOSP).
     * |valor| < 10_000 → ya está en mA (Samsung / varios OEM).
     */
    fun toMilliAmps(raw: Long): Int {
        val magnitude = abs(raw)
        val ma = if (magnitude >= 10_000L) raw / 1_000L else raw
        return ma.toInt()
    }
}
