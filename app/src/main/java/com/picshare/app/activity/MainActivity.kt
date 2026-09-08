package com.picshare.app.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.picshare.app.ui.AppMain
import com.picshare.app.ui.events.EventBus
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @Inject
  lateinit var eventBus: EventBus
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        0
      )
    }
    enableEdgeToEdge()
    setContent {
      AppMain(eventBus = eventBus)
    }
  }
}