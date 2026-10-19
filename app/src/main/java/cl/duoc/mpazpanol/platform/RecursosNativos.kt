package cl.duoc.mpazpanol.platform

import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Solo batería y vibración: no se requieren cámara, GPS ni permisos peligrosos. */
object RecursosNativos {
    fun bateriaPorcentaje(context: Context): Int? {
        val manager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager ?: return null
        val porcentaje = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return porcentaje.takeIf { it in 0..100 }
    }

    @Suppress("DEPRECATION")
    fun vibrarConfirmacion(context: Context) {
        val vibrador = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } ?: return

        if (!vibrador.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrador.vibrate(VibrationEffect.createOneShot(80L, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrador.vibrate(80L)
        }
    }
}
