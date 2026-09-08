package com.picshare.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
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
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
  }

}