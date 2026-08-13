package com.picshare.app.ui.feed

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.picshare.app.post.Gallery
import com.picshare.app.post.GalleryViewModel

@Composable
fun FeedScreen(
  viewModel: GalleryViewModel = hiltViewModel()
){

  Gallery(viewModel)
}