package com.picshare.app.ui

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.picshare.app.ui.navigation.AppNavHost
import com.picshare.app.ui.navigation.Header
import com.picshare.app.ui.navigation.NavEvent
import com.picshare.app.ui.navigation.NavigationFooter
import com.picshare.app.ui.navigation.NavigationViewModel
import com.picshare.app.ui.navigation.UploadButton
import com.picshare.app.ui.theme.PicshareTheme

@Composable
fun AppMain(
  navigationViewModel: NavigationViewModel = hiltViewModel()
){
  val navController = rememberNavController()

  val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
  navigationViewModel.onEvent(
    NavEvent.OnSetContent(
      activityNavController = navController,
    ){ backDispatcher?.onBackPressed() }
  )

  PicshareTheme {
    Scaffold(
      topBar = { Header() },
      modifier = Modifier.fillMaxSize(),
      bottomBar = { NavigationFooter(navController, navigationViewModel::onEvent) },
      floatingActionButton = { UploadButton(navigationViewModel::onEvent)},
      floatingActionButtonPosition = FabPosition.End

    ) { innerPadding ->
      AppNavHost(innerPadding, navigationViewModel)
    }
  }
}