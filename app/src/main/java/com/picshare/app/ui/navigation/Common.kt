package com.picshare.app.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.picshare.app.R
import compose.icons.CssGgIcons
import compose.icons.cssggicons.Image
import compose.icons.cssggicons.MathPlus
import compose.icons.cssggicons.Search
import compose.icons.cssggicons.User

@Composable
fun UploadButton(
  onNavigationEvent: (NavEvent) -> Unit,
){
  IconButton(
    modifier = Modifier
      .background(MaterialTheme.colorScheme.surface),
    onClick = {
      onNavigationEvent(
        NavEvent.OnNavigateToTab(
          Route.Upload
        )
      )
    },
  ) {
    Icon(
      imageVector = CssGgIcons.MathPlus,
      contentDescription = null
    )
  }
}
@Composable
fun NavigationFooter(
  navController: NavController,
  onNavigationEvent: (NavEvent) -> Unit
){
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination

  NavigationBar {
    Destination.entries.forEachIndexed { _, destination ->
      NavigationBarItem(
        selected = currentDestination?.route == destination.route::class.qualifiedName,
        onClick = {
          onNavigationEvent(
            NavEvent.OnNavigateTo(
              destination.route
            )
          )
        },
        icon = { Icon(imageVector = destination.icon, contentDescription = null) },
        label = { Text(destination.label) }
      )
    }
  }
}

enum class Destination constructor(
  val label: String,
  val icon: ImageVector,
  val route: Route
) {
  FEED(label = "Feed", icon = CssGgIcons.Image, route = Route.Feed),
  SEARCH(label = "Search", icon = CssGgIcons.Search, route = Route.Search),
  USER(label = "User", icon = CssGgIcons.User, route = Route.User())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header() {
  CenterAlignedTopAppBar(
    modifier = Modifier
      .fillMaxWidth(),
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.background
    ),
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Image(
          painter = painterResource(R.drawable.logo_image),
          contentDescription = "Logo",
          modifier = Modifier.size(24.dp),
          contentScale = ContentScale.Fit
        )
        Image(
          painter = painterResource(R.drawable.logo_text),
          contentDescription = "Picshare",
          modifier = Modifier.height(18.dp),
          contentScale = ContentScale.Fit
        )
      }
    }
  )
}