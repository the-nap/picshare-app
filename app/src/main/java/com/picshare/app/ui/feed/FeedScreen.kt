package com.picshare.app.ui.feed

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.picshare.app.ui.post.gallery.Gallery
import com.picshare.app.ui.post.PostViewModel

@Composable
fun FeedScreen(
  viewModel: PostViewModel = hiltViewModel()
){

  Gallery(viewModel)
}