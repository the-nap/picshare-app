package com.picshare.app.ui

import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.picshare.app.ui.events.AppEvent
import com.picshare.app.ui.events.EventBus
import com.picshare.app.ui.navigation.AppNavHost
import com.picshare.app.ui.navigation.Header
import com.picshare.app.ui.navigation.NavEvent
import com.picshare.app.ui.navigation.NavigationFooter
import com.picshare.app.ui.navigation.NavigationViewModel
import com.picshare.app.ui.navigation.UploadButton
import com.picshare.app.ui.theme.PicshareTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun AppMain(
  navigationViewModel: NavigationViewModel = hiltViewModel(),
  eventBus: EventBus
){

  val context = LocalContext.current
  LaunchedEffect(Unit){
    eventBus.events.filterIsInstance<AppEvent.Message>().collect { event ->
      Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
    }

  }
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