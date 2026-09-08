package com.picshare.app.ui.post.upload

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.picshare.app.R

class UploadService: Service() {

  override fun onBind(p0: Intent?): IBinder?{
    return null
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when(intent?.action) {
      Actions.UPLOAD.toString() -> startUpload()
    }

    return super.onStartCommand(intent, flags, startId)
  }

  private fun startUpload() {
    val notification = NotificationCompat.Builder(this, "upload_channel")
      .setSmallIcon(R.drawable.logo_complete)
      .setContentTitle("Uploading...")
      .setContentText("Elapsed Time: 00:00")
      .build()
    startForeground(1, notification)

  }

  enum class Actions {
    UPLOAD
  }
}