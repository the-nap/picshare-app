package com.picshare.app.ui.feed

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.picshare.app.ui.post.gallery.Gallery
import com.picshare.app.ui.post.SelectedPostViewModel

@Composable
fun FeedScreen(
  viewModel: SelectedPostViewModel = hiltViewModel()
){

  Gallery(viewModel)
}