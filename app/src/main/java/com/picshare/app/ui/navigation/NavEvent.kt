package com.picshare.app.ui.navigation

import androidx.navigation.NavHostController

sealed interface NavEvent {

  data class OnSetContent(
    val activityNavController: NavHostController,
    val onBackPressed: () -> Unit
  ): NavEvent

  data class OnNavigateTo(val destination: Route): NavEvent

  data object OnBack: NavEvent

}