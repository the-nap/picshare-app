package com.picshare.app.ui.post.upload

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.picshare.app.R
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.data.model.PostModel
import com.picshare.app.data.repository.PostRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UploadService: Service() {

  @Inject lateinit var postRepository: PostRepository

  private val serviceJob = SupervisorJob()
  private val serviceScope = CoroutineScope(Dispatchers.Main.immediate + serviceJob)
  override fun onBind(p0: Intent?): IBinder? = null

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    UploadStatusBus.update(UploadStatus.InProgress)
    when(intent?.action) {
      Actions.UPLOAD.toString() -> startUpload(intent)
    }
    return super.onStartCommand(intent, flags, startId)
  }

  @SuppressLint("MissingPermission")
  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  private fun startUpload(intent: Intent) {
    val notification = NotificationCompat.Builder(this, "upload_channel")
      .setSmallIcon(R.drawable.logo_complete)
      .setContentTitle("Uploading...")
      .setContentText("Your picture is being posted")
      .build()
    startForeground(1, notification)

    val uri = intent.getParcelableExtra("uri", Uri::class.java) ?: return stopError()
    val sizeBytes = intent.getLongExtra("sizeBytes", -1L)
    val post = intent.getParcelableExtra("post", PostModel::class.java) ?: return stopError()

    serviceScope.launch ( CoroutineExceptionHandler{_, throwable ->
      Log.e(this.toString(), "unhandled error", throwable)
      stopError()
    }) {
      when(val result = postRepository.upload(uri, sizeBytes, post)){
        is NetworkResult.Success -> {
          UploadStatusBus.update(UploadStatus.Success)
          stopSuccess()
        }
        is NetworkResult.Error -> {
          Log.e(this.toString(), "error: ${result.message}")
          UploadStatusBus.update(UploadStatus.Error(result.message))
          stopError()
        }
      }
    }
  }

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  private fun showResultNotification(title: String, text: String){
    val notification = NotificationCompat.Builder(this, "upload_channel")
      .setSmallIcon(R.drawable.logo_complete)
      .setContentTitle(title)
      .setContentText(text)
      .setAutoCancel(true)
      .build()

    NotificationManagerCompat.from(this).notify(1, notification)
    stopForeground(STOP_FOREGROUND_DETACH)
    stopSelf()
  }
  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  private fun stopSuccess() {
    showResultNotification("Upload completed", "Your picture is now online!")
  }
  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  private fun stopError() {
    showResultNotification("Upload failed", "We had problems during upload")
  }
  enum class Actions {
    UPLOAD
  }
}