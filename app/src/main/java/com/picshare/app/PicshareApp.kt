package com.picshare.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PicshareApp: Application() {
  override fun onCreate() {
    super.onCreate()
    val channel = NotificationChannel(
      "upload_channel",
      "Running Notifications",
      NotificationManager.IMPORTANCE_HIGH
    )
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
  }

}