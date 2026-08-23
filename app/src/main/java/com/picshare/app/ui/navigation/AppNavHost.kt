package com.picshare.app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.picshare.app.ui.feed.FeedScreen
import com.picshare.app.ui.post.post.PostScreen
import com.picshare.app.ui.post.upload.PostUploadScreen
import com.picshare.app.ui.search.SearchScreen
import com.picshare.app.ui.user.settings.SettingsScreen
import com.picshare.app.ui.user.user_screen.UserScreen

@Composable
fun AppNavHost(
  padding: PaddingValues,
  navigationViewModel: NavigationViewModel
){


  NavHost(
    navController = navigationViewModel.activityNavController,
    startDestination = Route.Feed,
    modifier = Modifier.padding(padding)
  ){
    composable<Route.Feed> { FeedScreen(onNavigationEvent = navigationViewModel::onEvent) }
    composable<Route.Search> { SearchScreen(onNavigationEvent = navigationViewModel::onEvent) }
    composable<Route.Settings> { SettingsScreen() }
    composable<Route.Upload> { PostUploadScreen() }
    composable<Route.User> { backStackEntry ->
      val route = backStackEntry.toRoute<Route.User>()
      UserScreen(onNavigationEvent = navigationViewModel::onEvent, userId = route.userId) }
    dialog<Route.Post>(
      dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
    ) { backStackEntry ->
      val route = backStackEntry.toRoute<Route.Post>()
      PostScreen(postId = route.postId, onNavigationEvent = navigationViewModel::onEvent) }
  }
}