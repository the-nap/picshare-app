package com.picshare.app.context_awareness.sensors

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor

data class ShakeSensors(
  val context: Context
): AndroidSensor(
  context = context,
  sensorFeature = PackageManager.FEATURE_SENSOR_ACCELEROMETER,
  sensorType = Sensor.TYPE_ACCELEROMETER
)
