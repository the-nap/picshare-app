package com.picshare.app.context_awareness

import android.content.Context
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.sqrt

class ShakeDetector(
  context: Context,
  private val shakeThreshold: Float = 36f, // m/s², tune this
  private val shakeSlopTimeMs: Long = 500  // min time between shakes
) {
  private val shakeSensor = ShakeSensors(context)
  private var lastShakeTime: Long = 0
  private var onShake: (() -> Unit)? = null

  fun setOnShakeListener(listener: () -> Unit) {
    onShake = listener
  }

  fun startListening() {
    shakeSensor.setOnSensorValuesChangedListener { values ->
      val x = values[0]
      val y = values[1]
      val z = values[2]

      // total acceleration minus gravity
      val gX = x / SensorManager.GRAVITY_EARTH
      val gY = y / SensorManager.GRAVITY_EARTH
      val gZ = z / SensorManager.GRAVITY_EARTH
      val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

      if (gForce > shakeThreshold / SensorManager.GRAVITY_EARTH) {
        Log.d("ShakeDetector", "gForce: $gForce")
        val now = System.currentTimeMillis()
        if (now - lastShakeTime > shakeSlopTimeMs) {
          lastShakeTime = now
          onShake?.invoke()
        }
      }
    }
    shakeSensor.startListening()
  }

  fun stopListening() {
    shakeSensor.stopListening()
  }
}
