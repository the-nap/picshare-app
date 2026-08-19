package com.picshare.app.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(): ViewModel() {
  private lateinit var onBackPressed: () -> Unit

  lateinit var activityNavController: NavHostController
    private set

  fun onEvent(event: NavEvent) {
    when (event) {
      is NavEvent.OnSetContent -> {
        activityNavController = event.activityNavController
        onBackPressed = event.onBackPressed
      }

      is NavEvent.OnBack -> onBackPressed()
      is NavEvent.OnNavigateTo -> activityNavController.navigate(event.destination)
    }
  }
}