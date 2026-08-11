package com.picshare.app.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.picshare.app.ui.feed.FeedScreen
import com.picshare.app.ui.search.SearchScreen
import com.picshare.app.ui.user.UserScreen

@Composable
fun AppNavHost(navController: NavHostController, padding: PaddingValues){
  NavHost(
    navController = navController,
    startDestination = Feed,
    modifier = Modifier.padding(padding)
  ){
    composable<Feed> { FeedScreen() }
    composable<Search> { SearchScreen() }
    composable<User> { UserScreen() }
  }
}