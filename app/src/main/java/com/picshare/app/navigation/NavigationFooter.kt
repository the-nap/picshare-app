package com.picshare.app.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import compose.icons.CssGgIcons
import compose.icons.cssggicons.Image
import compose.icons.cssggicons.Search
import compose.icons.cssggicons.User
import kotlinx.serialization.ExperimentalSerializationApi

@Composable
fun NavigationFooter(
  navController: NavController
){
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination

  NavigationBar {
    Destination.entries.forEachIndexed { _, destination ->
      NavigationBarItem(
        selected = currentDestination?.route == destination.route::class.qualifiedName,
        onClick = {
          navController.navigate(route = destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
              saveState = true
            }
            launchSingleTop = true
            restoreState = true
          }
        },
        icon = { Icon(imageVector = destination.icon, contentDescription = null) },
        label = { Text(destination.label) }
      )
    }
  }
}

enum class Destination @OptIn(ExperimentalSerializationApi::class) constructor(
  val label: String,
  val icon: ImageVector,
  val route: Any
) {
  FEED(label = "Feed", icon = CssGgIcons.Image, route = Feed),
  SEARCH(label = "Search", icon = CssGgIcons.Search, route = Search),
  USER(label = "User", icon = CssGgIcons.User, route = User)
}