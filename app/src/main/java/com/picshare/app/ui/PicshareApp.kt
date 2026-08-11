package com.picshare.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.picshare.app.navigation.AppNavHost
import com.picshare.app.ui.theme.PicshareTheme

@Composable
fun PicshareApp(){
  val navController = rememberNavController()
  PicshareTheme {
    Scaffold(
      topBar = { Header() },
      modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
      AppNavHost(navController, innerPadding)
    }
  }
}