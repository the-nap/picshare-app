package com.picshare.app.ui

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.picshare.app.ui.events.AppEvent
import com.picshare.app.ui.events.EventBus
import com.picshare.app.ui.navigation.AppNavHost
import com.picshare.app.ui.navigation.Header
import com.picshare.app.ui.navigation.NavEvent
import com.picshare.app.ui.navigation.NavigationFooter
import com.picshare.app.ui.navigation.NavigationViewModel
import com.picshare.app.ui.navigation.SideNavigation
import com.picshare.app.ui.navigation.UploadButton
import com.picshare.app.ui.theme.PicshareTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun AppMain(
  navigationViewModel: NavigationViewModel = hiltViewModel(),
  eventBus: EventBus
){

  val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

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
      topBar = { if(!isLandscape) Header() },
      modifier = Modifier.fillMaxSize(),
      bottomBar = { if(!isLandscape) NavigationFooter(navController, navigationViewModel::onEvent) },
      floatingActionButton = { UploadButton(navigationViewModel::onEvent)},
      floatingActionButtonPosition = FabPosition.End

    ) { innerPadding ->
      Row(Modifier
        .fillMaxSize()
        .padding(innerPadding)) {
        if (isLandscape) {
          SideNavigation(navController, navigationViewModel::onEvent)
        }
        AppNavHost(PaddingValues(0.dp), navigationViewModel)
      }
    }
  }
}