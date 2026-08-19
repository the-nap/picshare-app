package com.picshare.app.ui.post.post

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.ImageLoader
import com.picshare.app.ui.post.PostViewModel

@Composable
fun PostScreen(
  viewModel: PostViewModel = hiltViewModel(),
  imageLoader: ImageLoader = viewModel.imageLoader
){

  Text("test")
}