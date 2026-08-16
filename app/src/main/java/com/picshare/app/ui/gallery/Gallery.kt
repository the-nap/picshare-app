package com.picshare.app.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.picshare.app.BuildConfig

@Composable
fun Gallery (
  viewModel: GalleryViewModel = hiltViewModel(),
  key: String? = null,
  toSearch: String? = null
) {

  val context = LocalContext.current
  val gridState = rememberLazyStaggeredGridState()

  val state by viewModel.uiState.collectAsState()

  LaunchedEffect(key, toSearch) {
    viewModel.initialize(key, toSearch)
  }

  LaunchedEffect(gridState) {
    snapshotFlow {
      gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
    }.collect { lastVisibleIndex ->
      if (lastVisibleIndex != null &&
        lastVisibleIndex >= state.posts.size - 5){
        viewModel.loadNextPage()
      }
    }
  }

  when {
    !state.error.isNullOrBlank() ->
      Text(
        text = state.error.toString()
      )

    state.posts.isEmpty() ->
      Text(
        text = "No posts to show",
        modifier = Modifier
          .fillMaxWidth()
      )

    else ->
      LazyVerticalStaggeredGrid(
        state = gridState,
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize(),
        content = {
          items(state.posts) { post ->
            AsyncImage(
              model = ImageRequest.Builder(context)
                .data("${BuildConfig.PREVIEW_URL}/${post.id}")
                .crossfade(true)
                .build(),
              contentScale = ContentScale.Crop,
              contentDescription = null,
              modifier = Modifier
                .fillMaxWidth()
            )
          }
          if(state.isLoading){
            item (
              span = StaggeredGridItemSpan.FullLine
            ){
              Box(
                modifier = Modifier.fillMaxWidth()
                  .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center

              ){
                CircularProgressIndicator()
              }
            }
          }
        }
      )
  }
}