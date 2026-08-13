package com.picshare.app.ui.feed

import androidx.compose.runtime.Composable
import com.picshare.app.post.Gallery
import com.picshare.app.post.GalleryViewModel

@Composable
fun FeedScreen(
  viewModel: GalleryViewModel
){

  Gallery(viewModel)
}