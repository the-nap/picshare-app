package com.picshare.app.ui.feed

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.picshare.app.ui.navigation.NavEvent
import com.picshare.app.ui.post.gallery.Gallery
import com.picshare.app.ui.post.gallery.GalleryViewModel

@Composable
fun FeedScreen(
  viewModel: GalleryViewModel = hiltViewModel(),
  onNavigationEvent: (NavEvent) -> Unit
){

  Gallery(Modifier, viewModel, onNavigationEvent)
}